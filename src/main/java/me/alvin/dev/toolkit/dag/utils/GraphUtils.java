package me.alvin.dev.toolkit.dag.utils;

import org.jgrapht.Graph;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 3:42 PM
 */
public class GraphUtils {

    /**
     * 向图中添加节点及其关联
     *
     * @param graph
     * @param from
     * @param to
     * @param <V>
     * @param <E>
     */
    public static <V, E> void addEdge(Graph<V, E> graph, V from, V to) {
        graph.addVertex(from);
        graph.addVertex(to);
        graph.addEdge(from, to);
    }

}
