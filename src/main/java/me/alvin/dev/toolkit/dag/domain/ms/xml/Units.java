package me.alvin.dev.toolkit.dag.domain.ms.xml;

import java.util.List;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 11:22 AM
 */
public interface Units extends com.intellij.util.xml.DomElement {
    List<Unit> getUnits();
}
