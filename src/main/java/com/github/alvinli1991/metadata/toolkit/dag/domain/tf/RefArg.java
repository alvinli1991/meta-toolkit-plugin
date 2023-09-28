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
public class RefArg implements PyArgMeta {
    private PyRefMeta ref;

    @Override
    public ArgType getType() {
        return ArgType.ref;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefArg refArg = (RefArg) o;
        return Objects.equals(getRef(), refArg.getRef());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRef());
    }
}
