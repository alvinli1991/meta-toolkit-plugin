package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Objects;

/**
 * Date: 2023/9/19
 * Time: 2:14 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PyFuncMeta {
    private String funcName;

    private int funcSeq;

    @Singular("param")
    private List<PyParamMeta> params;

    @Singular("assign")
    private List<PyStatementMeta> assigns;

    @Singular("expression")
    private List<PyStatementMeta> expressions;


    private PyStatementMeta finalReturn;

    public TfPyFunctionType getTfPyFunctionType() {
        if (null == finalReturn) {
            return TfPyFunctionType.startFunction;
        } else {
            return TfPyFunctionType.refFunction;
        }
    }

    public String getFuncSeqTag() {
        return "f" + funcSeq + "_";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PyFuncMeta that = (PyFuncMeta) o;
        return Objects.equals(getFuncName(), that.getFuncName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFuncName());
    }

    public enum TfPyFunctionType {
        //tensorflow 起始函数
        startFunction,
        //其他函数
        refFunction
    }
}
