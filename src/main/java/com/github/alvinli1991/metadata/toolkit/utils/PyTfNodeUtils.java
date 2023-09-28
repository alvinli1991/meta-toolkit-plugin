package com.github.alvinli1991.metadata.toolkit.utils;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.Node;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.State;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.*;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.PyKey;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.PyNodeType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.impl.PyKeywordArgumentImpl;
import com.jetbrains.python.psi.impl.PyLambdaExpressionImpl;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 2023/9/21
 * Time: 11:17 AM
 */
public class PyTfNodeUtils {

    //region build meta
    public static PyCallExpressMeta transToMeta(PyCallExpression callExpr) {
        if (callExpr == null) {
            return null;
        }
        //解析入参
        PyExpression[] args = callExpr.getArguments();
        List<? extends PyArgMeta> argMetas = Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof PyReferenceExpression) {
                        return RefArg.builder()
                                .ref(PyRefMeta.builder()
                                        .ref(arg.getName())
                                        .build())
                                .build();
                    } else if (arg instanceof PyKeywordArgumentImpl) {
                        String value = "";
                        PyStringLiteralExpression strLiteral = PsiTreeUtil.findChildOfType(arg, PyStringLiteralExpression.class);
                        if (strLiteral != null) {
                            value = strLiteral.getStringValue();
                        }

                        PyBoolLiteralExpression boolLiteral = PsiTreeUtil.findChildOfType(arg, PyBoolLiteralExpression.class);
                        if (boolLiteral != null) {
                            value = boolLiteral.getText();
                        }
                        return KvArg.builder()
                                .key(arg.getName())
                                .value(value)
                                .build();
                    } else if (arg instanceof PyLambdaExpressionImpl) {
                        return LambdaArg.builder()
                                .lambda(transToMeta(PsiTreeUtil.findChildOfType(arg, PyCallExpression.class)))
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();


        PyRefMeta funcCallRef = null;
        PyExpression pyExpression = callExpr.getCallee();
        if (pyExpression instanceof PyReferenceExpression) {
            funcCallRef = buildChainFuncCallRef((PyReferenceExpression) pyExpression);
        }

        PyCallExpressMeta.PyCallExpressMetaBuilder callBuilder = PyCallExpressMeta.builder()
                .callRef(funcCallRef)
                .args(argMetas);

        return callBuilder.build();
    }

    public static PyRefMeta buildChainFuncCallRef(PyReferenceExpression pyExpression) {
        PyRefMeta.PyRefMetaBuilder funcCallRefBuilder = PyRefMeta.builder()
                //解析return的调用
                .ref(Optional.of(pyExpression).map(PyExpression::getName).orElse(""));
        PsiElement child = pyExpression.getFirstChild();
        if (child instanceof PyReferenceExpression) {
            funcCallRefBuilder.preRef(buildChainFuncCallRef((PyReferenceExpression) child));
        }
        return funcCallRefBuilder.build();
    }

    public static PyStatementMeta transToMeta(PyAssignmentStatement assign, PyFuncMeta funcMeta, int statementSeq) {
        if (assign == null) {
            return null;
        }
        PyStatementMeta.PyStatementMetaBuilder statementBuilder = PyStatementMeta.builder()
                .type(PyStatementMeta.StatementType.assignStatement)
                .statementSeq(statementSeq);
        if (null != funcMeta) {
            statementBuilder.parentFuncRef(new WeakReference<>(funcMeta));
        }
        //等号左边
        statementBuilder.targets(Arrays.stream(assign.getTargets())
                .map(target -> PyRefMeta.builder()
                        .ref(target.getName())
                        .build())
                .toList());
        //等号右边
        PyExpression assignExp = assign.getAssignedValue();
        if (assignExp instanceof PyCallExpression) {
            statementBuilder.callExpress(transToMeta((PyCallExpression) assignExp));
        }
        //comment
        statementBuilder.comment(getCommentOfElement(assign));
        PyStatementMeta statementMeta = statementBuilder.build();
        //构造输入输出
        statementMeta.extractInputsAndOutputs();
        return statementMeta;
    }

    public static PyStatementMeta transToMeta(PyExpressionStatement expression, PyFuncMeta funcMeta, int statementSeq) {
        if (expression == null) {
            return null;
        }
        PyStatementMeta.PyStatementMetaBuilder statementBuilder = PyStatementMeta.builder()
                .type(PyStatementMeta.StatementType.expressionStatement)
                .statementSeq(statementSeq);
        if (null != funcMeta) {
            statementBuilder.parentFuncRef(new WeakReference<>(funcMeta));
        }

        PyExpression assignExp = expression.getExpression();
        if (assignExp instanceof PyCallExpression) {
            statementBuilder.callExpress(transToMeta((PyCallExpression) assignExp));
        }
        //comment
        statementBuilder.comment(getCommentOfElement(expression));
        PyStatementMeta statementMeta = statementBuilder.build();
        //构造输入输出
        statementMeta.extractInputsAndOutputs();
        return statementMeta;
    }

    /**
     * 当lambda引用的是kernels方法时
     *
     * @param containerStatementMeta
     * @param lambdaCallMeta
     * @param funcMeta
     * @return
     */
    public static PyStatementMeta transLambdaKernelsMetaToExpressionMeta(PyStatementMeta containerStatementMeta, PyCallExpressMeta lambdaCallMeta, PyFuncMeta funcMeta) {
        if (lambdaCallMeta == null) {
            return null;
        }
        PyStatementMeta.PyStatementMetaBuilder statementBuilder = PyStatementMeta.builder()
                .type(PyStatementMeta.StatementType.lambdaExpressionStatement);
        if (null != funcMeta) {
            statementBuilder.parentFuncRef(new WeakReference<>(funcMeta));
        }
        if (null != containerStatementMeta) {
            statementBuilder.parentStatementRef(new WeakReference<>(containerStatementMeta));
        }

        statementBuilder.callExpress(lambdaCallMeta);
        //comment
        PyStatementMeta statementMeta = statementBuilder.build();
        //构造输入输出
        statementMeta.extractInputsAndOutputs();
        return statementMeta;
    }


    public static PyStatementMeta transToMeta(PyReturnStatement returnStatement, PyFuncMeta funcMeta, int statementSeq) {
        if (returnStatement == null) {
            return null;
        }
        PyStatementMeta.PyStatementMetaBuilder statementBuilder = PyStatementMeta.builder()
                .type(PyStatementMeta.StatementType.returnStatement)
                .statementSeq(statementSeq);
        if (null != funcMeta) {
            statementBuilder.parentFuncRef(new WeakReference<>(funcMeta));
        }

        List<PyCallExpression> returnExpresses = new ArrayList<>(PsiTreeUtil.collectElementsOfType(returnStatement, PyCallExpression.class));
        PyCallExpressMeta theReturnExpression = returnExpresses.stream()
                .map(PyTfNodeUtils::transToMeta)
                .findFirst()
                .orElse(null);
        statementBuilder.callExpress(theReturnExpression);

        PyReferenceExpression singleReturnRef = PsiTreeUtil.findChildOfType(returnStatement, PyReferenceExpression.class);
        if (singleReturnRef != null) {
            statementBuilder.returnRef(PyRefMeta.builder()
                    .ref(singleReturnRef.getName())
                    .build());
        }


        //comment
        statementBuilder.comment(getCommentOfElement(returnStatement));
        PyStatementMeta statementMeta = statementBuilder.build();
        //构造输入输出
        statementMeta.extractInputsAndOutputs();
        return statementMeta;
    }

    public static String getCommentOfElement(PsiElement element) {
        if (null == element) {
            return "";
        }
        PsiElement prevElement = element;
        List<String> comments = new ArrayList<>();
        while (true) {
            prevElement = prevElement.getPrevSibling();
            if (null == prevElement) {
                break;
            }
            if (prevElement instanceof PsiWhiteSpace && prevElement.getText().contains("\n")) {
                continue;
            }
            if (prevElement instanceof PyStatement) {
                break;
            }

            if (prevElement instanceof PsiComment) {
                comments.add(prevElement.getText());
            }
        }
        return comments.stream()
                .filter(comment -> !comment.contains("-"))
                .map(comment -> StringUtils.strip(comment, "#"))
                .collect(Collectors.joining("。"));
    }
    //endregion


    //region build node
    public static Node transToNode(PyFuncMeta func) {
        Node funcNode = new Node(func.getFuncName(), PyNodeType.function.name(), "");
        funcNode.putData(PyKey.tfFunctionType.name(), func.getTfPyFunctionType().name());
        return funcNode;
    }

    public static Node transToNode(PyStatementMeta pyStatement, PyNodeClass callActionClzInfo, String parentFuncName) {
        if (pyStatement.isTfCondStatement()) {
            Node condNode = new Node(pyStatement.getStatementUniqueId(), PyNodeType.condition.name(), "");
            condNode.putData(PyKey.lambdas.name(), pyStatement.getLambdaArgs()
                    .stream()
                    .map(LambdaArg::getLambdaLastFunctionCallName)
                    .collect(Collectors.joining(",")));
            condNode.putData(PyKey.statementType.name(), pyStatement.getType().name());
            condNode.putData(PyKey.parentFunction.name(), parentFuncName);
            condNode.putData(PyKey.refFuncCallName.name(), pyStatement.getStatementLastFunctionCallName());

            condNode.putData(PyKey.inputs.name(), String.join(",", Optional.ofNullable(pyStatement.getInputs())
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(IoMeta::getData)
                    .collect(Collectors.toSet())));
            condNode.putData(PyKey.outputs.name(), String.join(",", Optional.ofNullable(pyStatement.getOutputs())
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(IoMeta::getData)
                    .collect(Collectors.toSet()))
            );
            condNode.putData(PyKey.comment.name(), pyStatement.getComment());
            return condNode;
        } else if (pyStatement.isFuncRefStatement()) {
            Node funRefNode = new Node(pyStatement.getStatementUniqueId(), PyNodeType.functionRef.name(), "");
            funRefNode.putData(PyKey.refFuncCallName.name(), pyStatement.getStatementLastFunctionCallName());
            funRefNode.putData(PyKey.parentFunction.name(), parentFuncName);
            funRefNode.putData(PyKey.statementType.name(), pyStatement.getType().name());

            funRefNode.putData(PyKey.inputs.name(), String.join(",", Optional.ofNullable(pyStatement.getInputs())
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(IoMeta::getData)
                    .collect(Collectors.toSet()))
            );
            funRefNode.putData(PyKey.outputs.name(), String.join(",", Optional.ofNullable(pyStatement.getOutputs())
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(IoMeta::getData)
                    .collect(Collectors.toSet()))
            );
            funRefNode.putData(PyKey.comment.name(), pyStatement.getComment());
            return funRefNode;
        } else if (pyStatement.isTfKernelsStatement()) {
            String className = Optional.ofNullable(callActionClzInfo).map(PyNodeClass::getClassName).orElse("");
            String desc = Optional.ofNullable(callActionClzInfo).map(PyNodeClass::getDesc).orElse("");

            Node theNode = new Node(pyStatement.getStatementUniqueId(), PyNodeType.operation.name(), desc);
            if (StringUtils.equals(PyStatementMeta.StatementType.lambdaExpressionStatement.name()
                    , pyStatement.getType().name())) {
                theNode.putData(PyKey.parentStatementId.name(), Optional.ofNullable(pyStatement.getParentStatementRef())
                        .map(WeakReference::get)
                        .map(PyStatementMeta::getStatementUniqueId)
                        .orElse(""));
            }
            theNode.putData(PyKey.clz.name(), className);
            theNode.putData(PyKey.statementType.name(), pyStatement.getType().name());
            theNode.putData(PyKey.refFuncCallName.name(), pyStatement.getStatementLastFunctionCallName());
            theNode.putData(PyKey.force.name(), pyStatement.getKvArgValue(PyKey.force.name()));
            theNode.putData(PyKey.parentFunction.name(), parentFuncName);
            theNode.putData(PyKey.inputs.name(), String.join(",", Optional.ofNullable(pyStatement.getInputs())
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(IoMeta::getData)
                    .collect(Collectors.toSet()))
            );
            theNode.putData(PyKey.outputs.name(), String.join(",", Optional.ofNullable(pyStatement.getOutputs())
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(IoMeta::getData)
                    .collect(Collectors.toSet()))
            );
            theNode.putData(PyKey.comment.name(), pyStatement.getComment());
            return theNode;
        } else {
            return null;
        }
    }
    //endregion


    //region build State
    public static State buildFuncState(Node funcNode, Map<String, List<Node>> funcCallNameToInnerNodes, Set<Node> funcOrLambdaExpressionOpNodes) {
        State.StateBuilder funcStatebuilder = State.builder();
        funcStatebuilder.name(funcNode.getId());
        funcStatebuilder.description(PyKey.dagType.name(), PyNodeType.function.name());
        funcCallNameToInnerNodes.get(funcNode.getId())
                .stream()
                //过滤掉独立的lambda表达式，这些需要再cond中用
                .filter(innerNode -> !StringUtils.equals(PyStatementMeta.StatementType.lambdaExpressionStatement.name()
                        , innerNode.getDataValue(PyKey.statementType.name(), "")))
                .forEach(innerNode -> {
                    String innerNodeType = innerNode.getType();
                    if (StringUtils.equals(PyNodeType.operation.name(), innerNodeType)) {
                        funcStatebuilder.child(buildOperationState(innerNode));
                    } else if (StringUtils.equals(PyNodeType.condition.name(), innerNodeType)) {
                        funcStatebuilder.child(buildConditionState(innerNode, funcCallNameToInnerNodes, funcOrLambdaExpressionOpNodes));
                    } else if (StringUtils.equals(PyNodeType.function.name(), innerNodeType)) {
                        funcStatebuilder.child(buildFuncState(innerNode, funcCallNameToInnerNodes, funcOrLambdaExpressionOpNodes));
                    } else if (StringUtils.equals(PyNodeType.functionRef.name(), innerNodeType)) {
                        State.StateBuilder funcRefStatebuilder = State.builder();
                        funcRefStatebuilder.name(innerNode.getId());
                        funcStatebuilder.description(PyKey.dagType.name(), PyNodeType.functionRef.name());
                        if (StringUtils.isNotBlank(innerNode.getDataValue(PyKey.comment.name(), ""))) {
                            funcRefStatebuilder.description(PyKey.comment.name(), innerNode.getDataValue(PyKey.comment.name(), ""));
                        }
                        if (StringUtils.isNotBlank(innerNode.getDataValue(PyKey.inputs.name(), ""))) {
                            funcRefStatebuilder.description(PyKey.inputs.name(), innerNode.getDataValue(PyKey.inputs.name(), ""));
                        }
                        if (StringUtils.isNotBlank(innerNode.getDataValue(PyKey.outputs.name(), ""))) {
                            funcRefStatebuilder.description(PyKey.outputs.name(), innerNode.getDataValue(PyKey.outputs.name(), ""));
                        }
                        funcOrLambdaExpressionOpNodes.stream()
                                .filter(node -> StringUtils.equals(PyNodeType.function.name(), node.getType()))
                                .filter(node -> StringUtils.equals(node.getId(), innerNode.getDataValue(PyKey.refFuncCallName.name(), "")))
                                .findFirst()
                                .ifPresent(theRefFuncNode -> {
                                    funcRefStatebuilder.child(buildFuncState(theRefFuncNode, funcCallNameToInnerNodes, funcOrLambdaExpressionOpNodes));
                                });
                        funcStatebuilder.child(funcRefStatebuilder.build());
                    }
                });

        return funcStatebuilder.build();
    }

    public static State buildOperationState(Node opNode) {
        State.StateBuilder opStatebuilder = State.builder();
        opStatebuilder.name(opNode.getId());
        opStatebuilder.description(PyKey.dagType.name(), PyNodeType.operation.name());
        if (StringUtils.isNotBlank(opNode.getDataValue(PyKey.clz.name(), ""))) {
            opStatebuilder.description(PyKey.clz.name(), opNode.getDataValue(PyKey.clz.name(), ""));
        }

        if (StringUtils.isNotBlank(opNode.getDataValue(PyKey.comment.name(), ""))) {
            opStatebuilder.description(PyKey.comment.name(), opNode.getDataValue(PyKey.comment.name(), ""));
        }

        if (StringUtils.isNotBlank(opNode.getDataValue(PyKey.inputs.name(), ""))) {
            opStatebuilder.description(PyKey.inputs.name(), opNode.getDataValue(PyKey.inputs.name(), ""));
        }

        if (StringUtils.isNotBlank(opNode.getDataValue(PyKey.outputs.name(), ""))) {
            opStatebuilder.description(PyKey.outputs.name(), opNode.getDataValue(PyKey.outputs.name(), ""));
        }

        if (StringUtils.isNotBlank(opNode.getDataValue(PyKey.force.name(), ""))) {
            opStatebuilder.description(PyKey.force.name(), opNode.getDataValue(PyKey.force.name(), ""));
        }

        return opStatebuilder.build();
    }

    public static State buildConditionState(Node condNode, Map<String, List<Node>> funcCallNameToInnerNodes, Set<Node> funcOrLambdaExpressionOpNodes) {
        State.StateBuilder condStatebuilder = State.builder();
        condStatebuilder.name(condNode.getId());
        condStatebuilder.description(PyKey.dagType.name(), PyNodeType.condition.name());
        if (StringUtils.isNotBlank(condNode.getDataValue(PyKey.inputs.name(), ""))) {
            condStatebuilder.description(PyKey.inputs.name(), condNode.getDataValue(PyKey.inputs.name(), ""));
        }
        if (StringUtils.isNotBlank(condNode.getDataValue(PyKey.outputs.name(), ""))) {
            condStatebuilder.description(PyKey.outputs.name(), condNode.getDataValue(PyKey.outputs.name(), ""));
        }
        //get lambda info
        String[] conditionInnerLambdaNodeIds = StringUtils.split(condNode.getDataValue(PyKey.lambdas.name(), ""), ",");
        Set<String> lambdaCallRefNames = Arrays.stream(conditionInnerLambdaNodeIds)
                .collect(Collectors.toSet());
        funcOrLambdaExpressionOpNodes
                .stream()
                .filter(node -> {
                    if (StringUtils.equals(PyNodeType.function.name(), node.getType())) {
                        return lambdaCallRefNames.contains(node.getId());
                    } else if (StringUtils.equals(PyStatementMeta.StatementType.lambdaExpressionStatement.name()
                            , node.getDataValue(PyKey.statementType.name(), ""))) {
                        return lambdaCallRefNames.contains(node.getDataValue(PyKey.refFuncCallName.name(), ""));
                    } else {
                        return false;
                    }
                }).forEach(lambdaRefNode -> {
                    if (StringUtils.equals(PyNodeType.function.name(), lambdaRefNode.getType())) {
                        condStatebuilder.child(buildFuncState(lambdaRefNode, funcCallNameToInnerNodes, funcOrLambdaExpressionOpNodes));
                    } else if (StringUtils.equals(PyStatementMeta.StatementType.lambdaExpressionStatement.name()
                            , lambdaRefNode.getDataValue(PyKey.statementType.name(), ""))) {
                        condStatebuilder.child(buildOperationState(lambdaRefNode));
                    }
                });


        return condStatebuilder.build();
    }
    //endregion

}
