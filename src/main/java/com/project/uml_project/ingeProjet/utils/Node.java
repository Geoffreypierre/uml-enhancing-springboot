package com.project.uml_project.ingeProjet.utils;

import java.util.Collection;
import java.util.ArrayList;

//représente un noeud du graphe de connaissance
public class Node {

    private Collection<Node> children;
    private String name;
    private Collection<String> attribute;
    private Collection<String> method;

    public Node(Collection<Node> children) {
        this.children = children;
        this.attribute = new ArrayList<>();
        this.method = new ArrayList<>();
    }

    public void toCSV() {
        // TODO: Implement CSV export
    }

    public Collection<Node> getChildren() {
        return this.children;
    }

    public void setChildren(Collection<Node> children) {
        this.children = children;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Collection<String> attribute) {
        this.attribute = attribute;
    }

    public Collection<String> getMethod() {
        return this.method;
    }

    public void setMethod(Collection<String> method) {
        this.method = method;
    }

};
