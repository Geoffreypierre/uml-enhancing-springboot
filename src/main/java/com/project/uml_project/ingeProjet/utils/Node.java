package com.project.uml_project.ingeProjet.utils;

import java.util.Collection;


//représente un noeud du graphe de connaissance
public class Node {

    private Collection<Node> children;


    public Node(Collection<Node> children)
	{
		this.children = children;
    }


    public void toCSV() {

    };


    public Collection<Node> getChildren() {
        return this.children;
    }

    public void setChildren(Collection<Node> children) {
        this.children = children;
    }

	};


