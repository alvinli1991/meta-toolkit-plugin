package me.alvin.dev.toolkit.dag.domain.ms.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 2:04 PM
 */
public interface Depend extends com.intellij.util.xml.DomElement {
    @Attribute("id")
    GenericAttributeValue<String> getDependId();

//    default String getStageName() {
//        return getDependId().getValue() + "_stage";
//    }
}
