package com.github.alvinli1991.metadata.toolkit.dag.domain.ms.xml;

/**
 * Date: 2023/9/11
 * Time: 11:21 AM
 */
public interface Unit extends com.intellij.util.xml.DomElement {
    Id getId();

    Description getDescription();

    Clz getClz();
}
