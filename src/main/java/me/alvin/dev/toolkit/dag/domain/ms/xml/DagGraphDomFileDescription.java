package me.alvin.dev.toolkit.dag.domain.ms.xml;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 11:25 AM
 */
public class DagGraphDomFileDescription extends DomFileDescription<DagGraph> {

    public DagGraphDomFileDescription() {
        super(DagGraph.class, "DagGraph", "");
    }
}
