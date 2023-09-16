package com.github.alvinli1991.metadata.toolkit.dag.domain.ms.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Date: 2023/9/11
 * Time: 11:18 AM
 */
public interface DagGraph extends com.intellij.util.xml.DomElement {
    @Attribute("id")
    GenericAttributeValue<String> getDagId();

    Units getUnits();

    Stages getStages();
}
