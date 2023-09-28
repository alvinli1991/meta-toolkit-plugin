package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.PyKey;
import com.github.alvinli1991.metadata.toolkit.dag.domain.tf.biz.TfPyFileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 2023/9/19
 * Time: 2:29 PM
 */
@Data
@AllArgsConstructor
public class PyCallExpressMeta {

    private PyRefMeta callRef;

    @Singular("arg")
    private List<? extends PyArgMeta> args;

    //region 解析内容
    @JsonIgnore
    private Map<String, String> kvArg;

    @JsonIgnore
    private boolean gatherKv;

    //endregion

    @Builder
    public PyCallExpressMeta(PyRefMeta callRef, List<? extends PyArgMeta> args) {
        this();
        this.callRef = callRef;
        this.args = args;
    }

    public PyCallExpressMeta() {
        this.kvArg = new HashMap<>();
        this.gatherKv = false;
    }

    //region  identity

    /**
     * 是否是tf.cond调用
     *
     * @return
     */
    public boolean isTfCondFunc() {
        return StringUtils.equals(getLastFunctionCallName(), TfPyFileInfo.TF_COND);
    }

    public boolean isTfKernelsFunc() {
        return StringUtils.equals(getFirstFunctionRefName(), TfPyFileInfo.TF_KERNELS);
    }

    public boolean isFuncRef() {
        return !isTfCondFunc() && !isTfKernelsFunc();
    }

    /**
     * 获取用户定义的唯一id
     *
     * @return
     */
    public String getUserDefineId() {
        return getKvArgValue(PyKey.id.name());
    }


    public String getFirstFunctionRefName() {
        return Optional.ofNullable(callRef)
                .map(PyRefMeta::getPathFirstRef)
                .orElse("");
    }

    public String getLastFunctionCallName() {
        return Optional.ofNullable(callRef)
                .map(PyRefMeta::getRef)
                .orElse("");
    }

    public String getFullFunctionCallPath() {
        return Optional.ofNullable(callRef)
                .map(PyRefMeta::getFullRefPath)
                .orElse("");
    }
    //endregion


    //region arguments
    @JsonIgnore
    public List<LambdaArg> getLambdaArgs() {
        return Optional.ofNullable(args)
                .orElse(Collections.emptyList())
                .stream()
                .filter(pyArg -> pyArg.getType() == ArgType.lambda)
                .map(pyArg -> (LambdaArg) pyArg)
                .collect(Collectors.toList());
    }


    public String getKvArgValue(String key) {
        initKvMap();
        return this.kvArg.getOrDefault(key, "");
    }

    public boolean containsKvArg(String key) {
        initKvMap();
        return this.kvArg.containsKey(key);
    }


    private void initKvMap() {
        if (!gatherKv && MapUtils.isEmpty(kvArg)) {
            this.kvArg = Optional.ofNullable(args)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(arg -> arg instanceof KvArg)
                    .map(arg -> (KvArg) arg)
                    .collect(Collectors.toMap(KvArg::getKey, KvArg::getValue));
            this.gatherKv = true;
        }
    }

    public Set<IoMeta> getInputs() {
        Set<IoMeta> inputs = Optional.ofNullable(args)
                .orElse(Collections.emptyList())
                .stream()
                .filter(arg -> arg instanceof RefArg)
                .map(arg -> (RefArg) arg)
                .map(RefArg::getRef)
                .map(PyRefMeta::getRef)
                .map(input -> IoMeta.builder().data(input).build())
                .collect(Collectors.toSet());
        inputs.addAll(getLambdaArgs()
                .stream()
                .map(LambdaArg::getLambdaFuncInput)
                .flatMap(Collection::stream)
                .map(input -> IoMeta.builder().data(input.getData()).build())
                .collect(Collectors.toSet()));
        return inputs;
    }
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PyCallExpressMeta that = (PyCallExpressMeta) o;
        return Objects.equals(getCallRef(), that.getCallRef()) && Objects.equals(getArgs(), that.getArgs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCallRef(), getArgs());
    }
}
