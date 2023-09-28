package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Date: 2023/9/19
 * Time: 2:22 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PyParamMeta {

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PyParamMeta that = (PyParamMeta) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
