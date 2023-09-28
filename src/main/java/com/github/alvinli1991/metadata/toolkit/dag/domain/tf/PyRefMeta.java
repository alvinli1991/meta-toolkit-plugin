package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Date: 2023/9/19
 * Time: 2:43 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PyRefMeta {

    private String ref;

    private PyRefMeta preRef;


    public String getFullRefPath() {
        return this.toString();
    }

    @JsonIgnore
    public String getPathFirstRef() {
        PyRefMeta preRef = this;
        while (null != preRef.getPreRef()) {
            preRef = preRef.getPreRef();
        }
        return preRef.ref;
    }

    @Override
    public String toString() {
        String preRefStr = null == preRef ? null : StringUtils.trimToEmpty(preRef.toString());
        return null == preRefStr ? StringUtils.trimToEmpty(ref) : preRefStr + "." + StringUtils.trimToEmpty(ref);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PyRefMeta pyRef = (PyRefMeta) o;
        return Objects.equals(getRef(), pyRef.getRef()) && Objects.equals(getPreRef(), pyRef.getPreRef());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRef(), getPreRef());
    }
}
