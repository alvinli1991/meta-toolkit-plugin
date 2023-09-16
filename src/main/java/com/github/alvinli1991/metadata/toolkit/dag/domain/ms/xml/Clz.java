package com.github.alvinli1991.metadata.toolkit.dag.domain.ms.xml;

import org.apache.commons.lang3.StringUtils;

/**
 * Date: 2023/9/11
 * Time: 11:20 AM
 */
public interface Clz extends com.intellij.util.xml.DomElement {
    String getValue();

    default String getClzName() {
        return StringUtils.substringAfterLast(getValue(), ".");
    }
}
