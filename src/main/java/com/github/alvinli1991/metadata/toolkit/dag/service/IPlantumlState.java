package com.github.alvinli1991.metadata.toolkit.dag.service;

import com.github.alvinli1991.metadata.toolkit.dag.domain.common.Edge;
import com.github.alvinli1991.metadata.toolkit.dag.domain.common.GraphDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.common.LogicDag;
import com.github.alvinli1991.metadata.toolkit.dag.domain.plantuml.StateUml;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Date: 2023/9/19
 * Time: 21:28
 */
public interface IPlantumlState {

    default boolean hasCycle(LogicDag logicDag) {
        if (null == logicDag) {
            return false;
        }
        if (CollectionUtils.isEmpty(logicDag.getEdges())) {
            return false;
        }
        GraphDag graphDag = new GraphDag(logicDag.getId());
        try {
            for (Edge edge : logicDag.getEdges()) {
                if (null == edge.getSource() || null == edge.getTarget()) {
                    continue;
                }
                graphDag.addDependency(edge.getSource(), edge.getTarget());
            }
        } catch (IllegalArgumentException e) {
            return true;
        }

        return false;
    }

    StateUml buildPlantumlState(LogicDag logicDag);
}
