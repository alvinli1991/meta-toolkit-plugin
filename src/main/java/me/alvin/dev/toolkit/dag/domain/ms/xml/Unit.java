package me.alvin.dev.toolkit.dag.domain.ms.xml;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 11:21 AM
 */
public interface Unit extends com.intellij.util.xml.DomElement {
    Id getId();

    Description getDescription();

    Clz getClz();
}
