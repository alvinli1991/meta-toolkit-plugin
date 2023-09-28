package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.TfPyFileInfo;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 2023/9/19
 * Time: 2:16 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PyStatementMeta {

    private int statementSeq;

    //语句等号左边的变量
    @Singular("target")
    private List<PyRefMeta> targets;

    //语句等号右边的表达式
    private PyCallExpressMeta callExpress;

    //当statement类型是return，且return的只是个引用时。例如return x
    @Singular("returnRef")
    private List<PyRefMeta> returnRefs;

    private StatementType type;

    private WeakReference<PyFuncMeta> parentFuncRef;

    private WeakReference<PyStatementMeta> parentStatementRef;


    //region 解析信息
    private Set<IoMeta> inputs;

    private Set<IoMeta> outputs;

    private String comment;
    //endregion

    public String getStatementSeqTag() {
        return "s" + statementSeq + "_";
    }

    public List<PyRefMeta> getReturnRefs() {
        if (type != StatementType.returnStatement) {
            return Collections.emptyList();
        }
        if (null != callExpress) {
            return Collections.emptyList();
        }
        return returnRefs;
    }

    public Optional<PyFuncMeta> getParentFuncRef() {
        if (parentFuncRef == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(parentFuncRef.get());
    }

    public String getParentFuncName() {
        return Optional.ofNullable(parentFuncRef)
                .map(WeakReference::get)
                .map(PyFuncMeta::getFuncName)
                .orElse(TfPyFileInfo.OUTER_MOST_FUNC_TAG);
    }

    private String getParentFuncSeqTag() {
        return Optional.ofNullable(parentFuncRef)
                .map(WeakReference::get)
                .map(PyFuncMeta::getFuncSeqTag)
                .orElse(TfPyFileInfo.OUTER_MOST_FUNC_TAG);
    }

    public void extractInputsAndOutputs() {
        if (null == type) {
            return;
        }
        inputs = Optional.ofNullable(callExpress)
                .map(PyCallExpressMeta::getInputs)
                .orElse(Collections.emptySet())
                .stream()
                .peek(callInput -> callInput.setContainerFuncName(getParentFuncName()))
                .collect(Collectors.toSet());


        outputs = Optional.ofNullable(targets)
                .orElse(Collections.emptyList())
                .stream()
                .map(PyRefMeta::getRef)
                .map(input -> IoMeta.builder().containerFuncName(getParentFuncName()).data(input).build())
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public List<LambdaArg> getLambdaArgs() {
        return Optional.ofNullable(callExpress)
                .map(PyCallExpressMeta::getLambdaArgs)
                .orElse(Collections.emptyList());
    }

    public boolean isTfCondStatement() {
        return Optional.ofNullable(callExpress)
                .map(PyCallExpressMeta::isTfCondFunc)
                .orElse(false);
    }

    public boolean isTfKernelsStatement() {
        return Optional.ofNullable(callExpress)
                .map(PyCallExpressMeta::isTfKernelsFunc)
                .orElse(false);
    }

    public boolean isFuncRefStatement() {
        return Optional.ofNullable(callExpress)
                .map(PyCallExpressMeta::isFuncRef)
                .orElse(false);
    }

    public boolean isReturnRef() {
        return CollectionUtils.isNotEmpty(getReturnRefs());
    }

    /**
     * 获取能标识该语句的唯一id
     *
     * @return
     */
    public String getStatementUniqueId() {
        if (type == StatementType.returnStatement && null == callExpress) {
            return Optional.ofNullable(getReturnRefs())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(PyRefMeta::getRef)
                    .collect(Collectors.joining(","));
        } else {
            if (isTfCondStatement()) {
                return getCondFunctionUniqueCallName();
            } else if (isFuncRefStatement()) {
                String lastFunctionCallName = Optional.ofNullable(callExpress)
                        .map(PyCallExpressMeta::getLastFunctionCallName)
                        .orElse("");
                return getParentFuncSeqTag() + getStatementSeqTag() + lastFunctionCallName;
            } else if (isTfKernelsStatement()) {
                String userDefineId = Optional.ofNullable(callExpress)
                        .map(PyCallExpressMeta::getUserDefineId)
                        .orElse("");
                if (StringUtils.isNotBlank(userDefineId)) {
                    return userDefineId;
                } else {
                    String lastFunctionCallName = Optional.ofNullable(callExpress)
                            .map(PyCallExpressMeta::getLastFunctionCallName)
                            .orElse("");
                    return getParentFuncSeqTag() + getStatementSeqTag() + lastFunctionCallName;
                }
            } else {
                return "errorId";
            }
        }
    }

    /**
     * 获取cond方法的调用名
     *
     * @return
     */
    public String getCondFunctionUniqueCallName() {
        if (isTfCondStatement()) {
            String targetStr = Optional.ofNullable(targets)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(PyRefMeta::getRef)
                    .collect(Collectors.joining(","));
            return getParentFuncSeqTag() + getStatementSeqTag() + targetStr + TfPyFileInfo.TF_COND_SUFFIX;
        } else {
            return "";
        }

    }

    public String getStatementLastFunctionCallName() {
        return Optional.ofNullable(callExpress)
                .map(PyCallExpressMeta::getLastFunctionCallName)
                .orElse("");
    }

    public String getStatementFullCallPath() {
        return Optional.ofNullable(callExpress)
                .map(PyCallExpressMeta::getFullFunctionCallPath)
                .orElse("");
    }

    public String getKvArgValue(String key) {
        return Optional.ofNullable(callExpress).map(expr -> expr.getKvArgValue(key)).orElse("");
    }

    public boolean containsKvArg(String key) {
        return Optional.ofNullable(callExpress).map(expr -> expr.containsKvArg(key)).orElse(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PyStatementMeta that = (PyStatementMeta) o;
        return Objects.equals(getTargets(), that.getTargets()) && Objects.equals(getCallExpress(), that.getCallExpress()) && Objects.equals(getReturnRefs(), that.getReturnRefs()) && getType() == that.getType() && Objects.equals(getParentFuncName(), that.getParentFuncName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargets(), getCallExpress(), getReturnRefs(), getType(), getParentFuncName());
    }

    @Override
    public String toString() {
        return "PyStatementMeta{" +
                "statementSeq=" + statementSeq +
                ", targets=" + targets +
                ", callExpress=" + callExpress +
                ", returnRefs=" + returnRefs +
                ", type=" + type +
                '}';
    }

    public enum StatementType {
        assignStatement,
        returnStatement,
        expressionStatement,
        lambdaExpressionStatement,
    }
}
