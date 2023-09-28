package com.github.alvinli1991.metadata.toolkit.dag.service.impl;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.common.Node;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.PlantumlConstant;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.State;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.StateRelation;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.StateUml;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.*;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.PyKey;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.PyNodeType;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.TfPyFileInfo;
import com.github.alvinli1991.metadata.toolkit.dag.service.TfPlantumlStateService;
import com.github.alvinli1991.metadata.toolkit.notification.MetadataToolkitNotifications;
import com.github.alvinli1991.metadata.toolkit.utils.JavaPsiUtils;
import com.github.alvinli1991.metadata.toolkit.utils.PyTfNodeUtils;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.jetbrains.python.psi.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Date: 2023/9/19
 * Time: 21:30
 */
public class TfDagPlantumlStateService implements TfPlantumlStateService {
    private final Project project;

    public TfDagPlantumlStateService(Project project) {
        this.project = project;
    }

    @Override
    public PyFileMeta parse(PsiFile psiFile) {
        PyFile pyFile = (PyFile) psiFile;

        List<PyFunction> functions = new ArrayList<>(PsiTreeUtil.collectElementsOfType(pyFile, PyFunction.class));
        AtomicInteger funcSeq = new AtomicInteger(0);
        //first build empty FuncionMeta
        List<Pair<PyFunction, PyFuncMeta>> pyFuncAndMetas = functions.stream()
                .filter(pyFunc -> {
                    String funcName = pyFunc.getName();
                    return !StringUtils.startsWith(funcName, TfPyFileInfo.INNER_FUNC_PREFIX);
                })
                .map(pyFunc -> {
                    PyFuncMeta pyFuncMeta = new PyFuncMeta();
                    pyFuncMeta.setFuncName(pyFunc.getName());
                    pyFuncMeta.setFuncSeq(funcSeq.incrementAndGet());
                    return Pair.of(pyFunc, pyFuncMeta);
                })
                .toList();


        //fill function meta with inner elements.此处assignment和expression分别解析，无法保证assignment和expression引用重名变量时的正确性
        AtomicInteger statementSeq = new AtomicInteger(0);
        List<PyFuncMeta> pyFuncMetas = pyFuncAndMetas.stream()
                .map(funcAndMeta -> {
                    PyFunction func = funcAndMeta.getKey();
                    PyFuncMeta funcMeta = funcAndMeta.getValue();
                    //查询入参
                    List<PyParamMeta> params = Arrays.stream(func.getParameterList().getParameters()).map(param -> {
                        PyParamMeta.PyParamMetaBuilder paramBuilder = PyParamMeta.builder()
                                .name(param.getName());
                        return paramBuilder.build();
                    }).toList();
                    funcMeta.setParams(params);

                    //查询assignment语句
                    List<PyAssignmentStatement> assignments = new ArrayList<>(PsiTreeUtil.collectElementsOfType(func, PyAssignmentStatement.class));
                    List<PyStatementMeta> assignStateMetas = assignments.stream()
                            .map(assignment -> PyTfNodeUtils.transToMeta(assignment, funcMeta, statementSeq.incrementAndGet()))
                            .filter(statementMeta -> !StringUtils.equals(TfPyFileInfo.TF_PLACEHOLDER, statementMeta.getStatementLastFunctionCallName()))
                            .toList();
                    funcMeta.setAssigns(assignStateMetas);

                    //查询expression语句
                    List<PyExpressionStatement> expressions = new ArrayList<>(PsiTreeUtil.collectElementsOfType(func, PyExpressionStatement.class));
                    List<PyStatementMeta> expressionStateMetas = expressions.stream()
                            .map(expression -> PyTfNodeUtils.transToMeta(expression, funcMeta, statementSeq.incrementAndGet()))
                            .toList();
                    funcMeta.setExpressions(expressionStateMetas);

                    //查询return 语句//暂不支持多个return语句
                    List<PyReturnStatement> returns = new ArrayList<>(PsiTreeUtil.collectElementsOfType(func, PyReturnStatement.class));
                    if (CollectionUtils.isNotEmpty(returns)) {
                        PyReturnStatement returnStatement = returns.get(returns.size() - 1);
                        PyStatementMeta lastReturnMeta = PyTfNodeUtils.transToMeta(returnStatement, funcMeta, statementSeq.incrementAndGet());
                        funcMeta.setFinalReturn(lastReturnMeta);
                    }
                    return funcMeta;
                })
                .collect(Collectors.toList());

        AtomicInteger outerAssignSeq = new AtomicInteger(0);
        List<PyAssignmentStatement> outerAssigns = new ArrayList<>(PsiTreeUtil.findChildrenOfType(pyFile, PyAssignmentStatement.class)).stream()
                .filter(statement -> statement.getParent() instanceof PyFile)
                .toList();
        List<PyStatementMeta> outAssignStatements = outerAssigns.stream()
                .map(outerAssign -> PyTfNodeUtils.transToMeta(outerAssign, null, outerAssignSeq.incrementAndGet()))
                .filter(statementMeta -> null != statementMeta.getCallExpress() && !StringUtils.contains(statementMeta.getStatementFullCallPath(), TfPyFileInfo.TF)
                        && !StringUtils.contains(statementMeta.getStatementFullCallPath(), TfPyFileInfo.OS))
                .collect(Collectors.toList());

        return PyFileMeta.builder()
                .fileName(pyFile.getName())
                .functions(pyFuncMetas)
                .outerAssignments(outAssignStatements)
                .build();
    }

    @Override
    public List<PyNodeClass> find(PsiFile psiFile) {
        final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);
        Module module = ModuleUtil.findModuleForFile(psiFile);
        assert module != null;
        final GlobalSearchScope moduleScope = GlobalSearchScope.moduleScope(module);

        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiClass badgerAnno = javaPsiFacade.findClass(PyNodeInfoAnno.PY_NODE_INFO_ANNOTATION, allScope);
        if (Objects.isNull(badgerAnno)) {
            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("Class not exist " + PyNodeInfoAnno.PY_NODE_INFO_ANNOTATION, NotificationType.ERROR)
                    .notify(project);
            return Collections.emptyList();
        }
        Query<PsiClass> classQuery = AnnotatedElementsSearch.searchPsiClasses(badgerAnno, moduleScope);
        List<PsiClass> annoClasses = new ArrayList<>(classQuery.findAll());
        if (CollectionUtils.isEmpty(annoClasses)) {

            MetadataToolkitNotifications.META_DATA_GROUP
                    .createNotification("Class not found with annotations " + PyNodeInfoAnno.PY_NODE_INFO_ANNOTATION, NotificationType.ERROR)
                    .notify(project);
            return Collections.emptyList();
        }

        //增加几个代码里固定写死的action
        List<PyNodeClass> defaultActionClasses = TfPyFileInfo.DEFAULT_NAME_TO_CLASS.entrySet()
                .stream()
                .map(entry -> {
                    PsiClass defaultClz = javaPsiFacade.findClass(entry.getValue(), allScope);
                    if (null == defaultClz) {
                        return null;
                    } else {
                        return PyNodeClass.builder()
                                .className(defaultClz.getName())
                                .anno(PyNodeInfoAnno.builder()
                                        .name(entry.getKey())
                                        .build())
                                .build();
                    }
                }).filter(Objects::nonNull)
                .toList();


        List<PyNodeClass> projectClasses = annoClasses.stream()
                .map(psiClass -> {
                    PsiAnnotation badgerAnnoMeta = psiClass.getAnnotation(PyNodeInfoAnno.PY_NODE_INFO_ANNOTATION);
                    List<String> annoAttrKeys = Optional.ofNullable(badgerAnnoMeta).map(PsiAnnotation::getAttributes)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(JvmAnnotationAttribute::getAttributeName)
                            .toList();
                    Map<String, String> annoKvs = annoAttrKeys.stream()
                            .map(key -> {
                                String value = JavaPsiUtils.getAnnoAttributeValue(javaPsiFacade, badgerAnnoMeta.findAttributeValue(key));
                                return Pair.of(key, value);
                            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
                    return PyNodeClass.builder()
                            .className(psiClass.getName())
                            .anno(PyNodeInfoAnno.builder()
                                    .name(annoKvs.getOrDefault("name", ""))
                                    .input(annoKvs.getOrDefault("input", ""))
                                    .output(annoKvs.getOrDefault("output", ""))
                                    .configKeys(annoKvs.getOrDefault("configKeys", ""))
                                    .des(annoKvs.getOrDefault("des", ""))
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        projectClasses.addAll(defaultActionClasses);

        return projectClasses;
    }

    @Override
    public LogicDag trans(PyFileMeta pyFileMeta, List<PyNodeClass> pyNodeClasses) {
        if (null == pyFileMeta) {
            return null;
        }
        //check
        List<PyFuncMeta> functions = pyFileMeta.getFunctions();

        int funSize = CollectionUtils.size(functions);
        if (funSize == 0) {
            throw new IllegalArgumentException("函数实现不存在");
        }

        LogicDag logicDag = new LogicDag(pyFileMeta.getFileName());


        //call if to class
        Map<String, PyNodeClass> lastFuncCallNameToClass = Optional.ofNullable(pyNodeClasses)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(PyNodeClass::getLastFunctionCallNameInPy, Function.identity(), (a, b) -> a));
        //build node

        //build function node
        functions.forEach(func -> {
            Node funcNode = PyTfNodeUtils.transToNode(func);
            logicDag.addNode(funcNode);
        });


        //build function and inner node
        Map<PyFuncMeta, Map<PyStatementMeta, Node>> funcToInnerNodes = functions
                .stream()
                .map(refFunc -> {
                    Stream<PyStatementMeta> assignStream = Optional.of(refFunc)
                            .map(PyFuncMeta::getAssigns)
                            .orElse(Collections.emptyList())
                            .stream();
                    //提取lambda中也是kernels算子的调用
                    Stream<PyStatementMeta> lambdaExpressionInAssignStream = Optional.of(refFunc)
                            .map(PyFuncMeta::getAssigns)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(pyStatementMeta -> Pair.of(pyStatementMeta, pyStatementMeta.getLambdaArgs()
                                    .stream()
                                    .map(LambdaArg::getLambda)
                                    .filter(PyCallExpressMeta::isTfKernelsFunc)
                                    .collect(Collectors.toList())))
                            .map(pair -> pair.getValue().stream()
                                    .map(tfKernelsFunc -> PyTfNodeUtils.transLambdaKernelsMetaToExpressionMeta(pair.getKey(), tfKernelsFunc, refFunc))
                                    .collect(Collectors.toList()))
                            .flatMap(Collection::stream);


                    Stream<PyStatementMeta> returnStream = Optional.of(refFunc)
                            .map(PyFuncMeta::getFinalReturn)
                            .filter(statement -> !statement.isReturnRef())
                            .stream();

                    List<PyStatementMeta> returnStatements = Optional.of(refFunc)
                            .map(PyFuncMeta::getFinalReturn)
                            .filter(statement -> !statement.isReturnRef())
                            .stream()
                            .collect(Collectors.toList());

                    Stream<PyStatementMeta> experStream = Optional.of(refFunc)
                            .map(PyFuncMeta::getExpressions)
                            .orElse(Collections.emptyList())
                            .stream();
                    //union stream
                    Map<PyStatementMeta, Node> statementToNode = Stream.concat(Stream.concat(Stream.concat(assignStream
                                                    , lambdaExpressionInAssignStream)
                                            , returnStream)
                                    , experStream)
                            .filter(Objects::nonNull)
                            .map(pyStatementMeta -> {
                                String lastFunctionCallName = pyStatementMeta.getStatementLastFunctionCallName();
                                PyNodeClass callActionClzInfo = lastFuncCallNameToClass.getOrDefault(lastFunctionCallName, null);
                                Node assignNode = PyTfNodeUtils.transToNode(pyStatementMeta, callActionClzInfo, refFunc.getFuncName());
                                logicDag.addNode(assignNode);
                                return Pair.of(pyStatementMeta, assignNode);
                            })
                            .filter(pair -> null != pair.getValue())
                            .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> a, LinkedHashMap::new));
                    return Pair.of(refFunc, statementToNode);
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> a, LinkedHashMap::new));


        //---buid edge
        //get  output -> statements，由于python 的等式左边可以重名，因此同一个output名可以对应多个statement
        Map<IoMeta, List<PyStatementMeta>> outputToStatement = funcToInnerNodes.values()
                .stream()
                .flatMap(statementToNodeMap -> statementToNodeMap.keySet().stream())
                .filter(statement -> CollectionUtils.isNotEmpty(statement.getOutputs()))
                .map(statement -> Pair.of(new ArrayList<>(statement.getOutputs()), statement))
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    //List<Pair<IoMeta, PyStatementMeta>> result = new ArrayList<>();
                    //for (IoMeta ioMeta : entry.getKey()) {
                    //    for (PyStatementMeta statementMeta : entry.getValue()) {
                    //        result.add(Pair.of(ioMeta, statementMeta));
                    //    }
                    //}
                    //return result.stream();
                    return entry.getKey().stream()
                            .flatMap(ioMeta -> entry.getValue().stream()
                                    .map(statementMeta -> Pair.of(ioMeta, statementMeta)));

                })
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));

        // get all statement to node
        Map<PyStatementMeta, Node> statementToNode = funcToInnerNodes.values()
                .stream()
                .flatMap(statementToNodeMap -> statementToNodeMap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        // condition node 需要找function node的依赖，其它node正常按照assignmentStatement的依赖关系
        funcToInnerNodes.forEach((func, statementToNodeMap) -> {
            statementToNodeMap.forEach((statement, node) -> {
                statement.getInputs().forEach(input -> {
                    List<PyStatementMeta> inputStatements = outputToStatement.getOrDefault(input, null);
                    if (null != inputStatements) {
                        PyStatementMeta inputStatement = null;
                        if (inputStatements.size() == 1) {
                            inputStatement = inputStatements.get(0);
                        } else {
                            //取距离最近的那个statement
                            int distance = 100;
                            for (PyStatementMeta possibleInput : inputStatements) {
                                int theDistance = Math.abs(possibleInput.getStatementSeq() - statement.getStatementSeq());
                                if (theDistance < distance) {
                                    inputStatement = possibleInput;
                                    distance = theDistance;
                                }
                            }
                        }
                        Node inputNode = statementToNode.getOrDefault(inputStatement, null);
                        if (null != inputNode) {
                            logicDag.addEdge(inputNode, node);
                        }
                    }
                });

            });
        });


        return logicDag;
    }


    @Override
    public StateUml buildPlantumlState(LogicDag logicDag) {
        //create state
        //function内部的表达式node
        Map<String, List<Node>> funcCallNameToInnerNodes = logicDag.getNodes()
                .stream()
                .filter(node -> !StringUtils.equals(PyNodeType.function.name(), node.getType())
                        && StringUtils.isNotBlank(node.getDataValue(PyKey.parentFunction.name(), "")))
                .collect(Collectors.groupingBy(node -> node.getDataValue(PyKey.parentFunction.name(), ""), Collectors.toList()));

        Set<Node> funcOrLambdaExpressionOpNodes = logicDag.getNodes().stream()
                .filter(node -> StringUtils.equals(PyNodeType.function.name(), node.getType()) ||
                        StringUtils.equals(PyStatementMeta.StatementType.lambdaExpressionStatement.name()
                                , node.getDataValue(PyKey.statementType.name(), "")))
                .collect(Collectors.toSet());

        //从主function开始，递归构建state
        List<Node> startFuncNodes = logicDag.getNodes()
                .stream()
                .filter(node -> StringUtils.equals(PyNodeType.function.name(), node.getType()))
                .filter(node -> StringUtils.equals(PyFuncMeta.TfPyFunctionType.startFunction.name(), node.getDataValue(PyKey.tfFunctionType.name(), "")))
                .toList();

        if (CollectionUtils.size(startFuncNodes) == 0) {
            throw new IllegalArgumentException("无起点函数");
        }
        if (CollectionUtils.size(startFuncNodes) != 1) {
            throw new IllegalArgumentException("起点函数大于一个，请删除无关的无return的函数");
        }

        Node startFuncNode = startFuncNodes.get(0);

        State startFuncTreeState = PyTfNodeUtils.buildFuncState(startFuncNode, funcCallNameToInnerNodes, funcOrLambdaExpressionOpNodes);

        State dagState = State.builder().name("DAG")
                .child(startFuncTreeState)
                .build();

        //create relation
        List<StateRelation> stateRelations = logicDag.getEdges()
                .stream()
                .map(edge -> {
                    if (null == edge.getSource() || null == edge.getTarget()) {
                        return null;
                    }
                    return StateRelation.builder()
                            .from(Optional.ofNullable(edge.getSource())
                                    .map(source -> State.builder()
                                            .name(source.getId())
                                            .description(PlantumlConstant.OUTPUTS_TAG, source.getDataValue(PyKey.outputs.name(), ""))
                                            .build())
                                    .orElse(null))
                            .to(Optional.ofNullable(edge.getTarget())
                                    .map(source -> State.builder()
                                            .name(source.getId())
                                            .description(PlantumlConstant.OUTPUTS_TAG, source.getDataValue(PyKey.outputs.name(), ""))
                                            .build())
                                    .orElse(null))
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        return StateUml.builder()
                .state(dagState)
                .stateRelations(stateRelations)
                .build();
    }
}
