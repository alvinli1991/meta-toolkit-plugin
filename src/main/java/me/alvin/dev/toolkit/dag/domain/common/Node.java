package me.alvin.dev.toolkit.dag.domain.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

/**
 * @author: Li Xiang
 * Date: 2023/9/11
 * Time: 2:44 PM
 */
public class Node {

    private final String id;
    private final Map<String, String> data;
    private final Set<Node> children;
    private String type;
    private String desc;


    public Node(String id, String type, String desc) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.data = new HashMap<>();
        this.children = new HashSet<>();
    }

    public Node(String id, String type, Map<String, String> data) {
        this(id, type, "");
        putData(data);
    }

    public Node(String id, String type) {
        this(id, type, "");
    }

    public Node(String id) {
        this(id, "", "");
    }


    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean addChild(Node node) {
        if (Objects.isNull(node)) {
            return false;
        }
        return this.children.add(node);
    }

    public void addChildren(Collection<? extends Node> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        this.children.addAll(nodes);
    }


    public Set<Node> getChildren() {
        return Collections.unmodifiableSet(this.children);
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
        Node node = (Node) o;
        return Objects.equals(getId(), node.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


    @Override
    public String toString() {
        return id;
    }
}
