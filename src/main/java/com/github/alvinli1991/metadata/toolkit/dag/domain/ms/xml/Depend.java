package com.github.alvinli1991.metadata.toolkit.dag.domain.ms.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Date: 2023/9/11
 * Time: 2:04 PM
 */
public interface Depend extends com.intellij.util.xml.DomElement {
    @Attribute("id")
    GenericAttributeValue<String> getDependId();

    default String getStageId() {
        return getDependId().getValue() + "_stage";
    }
}
