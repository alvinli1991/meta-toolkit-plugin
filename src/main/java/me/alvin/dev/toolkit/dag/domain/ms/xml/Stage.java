package me.alvin.dev.toolkit.dag.domain.ms.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 2:03 PM
 */
public interface Stage extends com.intellij.util.xml.DomElement {

    @Attribute("id")
    GenericAttributeValue<String> getStageId();

    Flows getFlows();

    Depends getDepends();

    default String getStageName() {
        return getStageId().getValue() + "_stage";
    }
}
