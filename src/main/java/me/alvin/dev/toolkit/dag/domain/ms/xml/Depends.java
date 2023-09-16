package me.alvin.dev.toolkit.dag.domain.ms.xml;

import java.util.List;

/**
 * Date: 2023/9/11
 * Time: 2:06 PM
 */
public interface Depends extends com.intellij.util.xml.DomElement {
    List<Depend> getDepends();
}
