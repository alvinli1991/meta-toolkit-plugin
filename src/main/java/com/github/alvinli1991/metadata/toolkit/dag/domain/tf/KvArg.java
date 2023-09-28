package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Date: 2023/9/19
 * Time: 4:09 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KvArg implements PyArgMeta {

    private String key;

    private String value;

    @Override
    public ArgType getType() {
        return ArgType.kv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KvArg kvArg = (KvArg) o;
        return Objects.equals(getKey(), kvArg.getKey()) && Objects.equals(getValue(), kvArg.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}
