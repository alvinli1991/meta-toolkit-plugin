package me.alvin.dev.toolkit.dag.domain.ms.xml;

import java.util.List;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 2:02 PM
 */
public interface Flows extends com.intellij.util.xml.DomElement {

    List<Flow> getFlows();
}
