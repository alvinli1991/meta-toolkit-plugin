package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Date: 2023/9/19
 * Time: 4:09 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LambdaArg implements PyArgMeta {
    private PyCallExpressMeta lambda;

    public String getLambdaLastFunctionCallName() {
        return Optional.ofNullable(lambda)
                .map(PyCallExpressMeta::getLastFunctionCallName)
                .orElse("");
    }


    public Set<IoMeta> getLambdaFuncInput() {
        return Optional.ofNullable(lambda)
                .map(PyCallExpressMeta::getInputs)
                .orElse(Collections.emptySet());
    }

    @Override
    public ArgType getType() {
        return ArgType.lambda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LambdaArg lambdaArg = (LambdaArg) o;
        return Objects.equals(getLambda(), lambdaArg.getLambda());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLambda());
    }
}
