package me.alvin.dev.toolkit.dag.domain.ms.xml;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Date: 2023/9/11
 * Time: 1:56 PM
 */
public interface Flow extends com.intellij.util.xml.DomElement {

    @Attribute("from")
    GenericAttributeValue<String> getFrom();

    @Attribute("to")
    GenericAttributeValue<String> getTo();

}
