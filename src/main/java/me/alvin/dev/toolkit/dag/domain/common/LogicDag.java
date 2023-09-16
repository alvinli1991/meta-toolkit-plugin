package me.alvin.dev.toolkit.dag.domain.common;

import java.util.*;

/**
 * Date: 2023/9/11
 * Time: 3:51 PM
 */
public class LogicDag {
    private final String id;

    private final Set<Node> nodes;
    private final Map<String, Node> nodeIdMap;

    private final Set<Edge> edges;

    private final Map<String, Edge> edgeIdMap;


    public LogicDag(String id) {
        this.id = id;
        this.nodes = new HashSet<>();
        this.nodeIdMap = new HashMap<>();
        this.edges = new HashSet<>();
        this.edgeIdMap = new HashMap<>();
    }


    public String getId() {
        return id;
    }

    public Optional<Node> getNodeById(String id) {
        return Optional.ofNullable(this.nodeIdMap.getOrDefault(id, null));
    }

    public Optional<Edge> getEdgeById(String id) {
        return Optional.ofNullable(this.edgeIdMap.getOrDefault(id, null));
    }

    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(this.nodes);
    }

    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(this.edges);
    }

    public boolean addNode(Node node) {
        if (node == null) {
            return false;
        }
        if (this.nodeIdMap.containsKey(node.getId())) {
            return false;
        }
        this.nodeIdMap.put(node.getId(), node);
        return this.nodes.add(node);
    }

    public boolean addEdge(Node from, Node to) {
        if (null == from && null == to) {
            return false;
        }
        addNode(from);
        addNode(to);
        Edge theEdge = new Edge(from, to);
        if (this.edgeIdMap.containsKey(theEdge.getId())) {
            return false;
        }
        this.edgeIdMap.put(theEdge.getId(), theEdge);
        return this.edges.add(theEdge);
    }

}
