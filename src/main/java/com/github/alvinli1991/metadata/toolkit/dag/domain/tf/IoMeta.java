package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Date: 2023/9/23
 * Time: 21:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IoMeta {
    private String data;
    private String containerFuncName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IoMeta ioMeta = (IoMeta) o;
        return Objects.equals(data, ioMeta.data) && Objects.equals(containerFuncName, ioMeta.containerFuncName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, containerFuncName);
    }
}
