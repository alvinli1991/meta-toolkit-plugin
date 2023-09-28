package com.github.alvinli1991.metadata.toolkit.dag.domain.tf;

import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Date: 2023/9/19
 * Time: 7:29 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PyNodeClass {
    private PyNodeInfoAnno anno;
    private String className;

    public String getLastFunctionCallNameInPy() {
        String badgerName = Optional.ofNullable(anno)
                .map(PyNodeInfoAnno::getName)
                .orElse(null);
        String tfId;
        if (StringUtils.isBlank(badgerName)) {
            tfId = className;
        } else {
            tfId = badgerName;
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tfId);
    }

    public String getDesc() {
        return Optional.ofNullable(anno)
                .map(PyNodeInfoAnno::getDes)
                .orElse("");
    }
}
