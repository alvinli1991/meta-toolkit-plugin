package me.alvin.dev.toolkit.dag.domain.ms.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Date: 2023/9/11
 * Time: 2:03 PM
 */
public interface Stage extends com.intellij.util.xml.DomElement {

    @Attribute("id")
    GenericAttributeValue<String> getStageName();

    //    @Attribute("desc")
    GenericAttributeValue<String> getStageDesc();

    Flows getFlows();

    Depends getDepends();

    default String getStageId() {
        return getStageName().getValue() + "_stage";
    }
}
