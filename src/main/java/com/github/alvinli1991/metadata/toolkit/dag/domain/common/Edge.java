package com.github.alvinli1991.metadata.toolkit.dag.domain.common;

import org.apache.commons.collections4.MapUtils;

import java.util.*;

/**
 * Date: 2023/9/11
 * Time: 2:45 PM
 */

public class Edge {

    private final String id;

    private final Node source;

    private final Node target;
    private final Map<String, String> data;
    private String desc;


    public Edge(String id, Node source, Node target, String desc) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.desc = desc;
        this.data = new HashMap<>();
    }

    public Edge(Node source, Node target, String desc) {
        this(Optional.ofNullable(source).map(Node::getId).orElse("") + "-" + Optional.ofNullable(target).map(Node::getId).orElse(""),
                source,
                target,
                desc);
    }

    public Edge(Node source, Node target) {
        this(source, target, "");
    }

    public String getId() {
        return id;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String putData(String key, String value) {
        if (Objects.isNull(key)) {
            return null;
        }
        return this.data.put(key, value);
    }

    public void putData(Map<String, String> input) {
        if (MapUtils.isEmpty(input)) {
            return;
        }
        this.data.putAll(input);
    }

    public String getDataValue(String key, String defaultVal) {
        if (Objects.isNull(key)) {
            return defaultVal;
        }
        return this.data.getOrDefault(key, defaultVal);
    }

    public Map<String, String> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(getSource(), edge.getSource()) && Objects.equals(getTarget(), edge.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSource(), getTarget());
    }

    @Override
    public String toString() {
        return "(" + source + " -> " + target + ")";
    }
}
