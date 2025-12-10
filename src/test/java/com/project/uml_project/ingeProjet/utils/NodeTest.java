package com.project.uml_project.ingeProjet.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    private Node node;
    private Collection<Node> children;

    @BeforeEach
    void setUp() {
        children = new ArrayList<>();
        node = new Node(children);
    }

    @Test
    void testConstructor() {
        assertNotNull(node);
        assertNotNull(node.getChildren());
        assertEquals(0, node.getChildren().size());
    }

    @Test
    void testGetAndSetName() {
        String name = "TestNode";
        node.setName(name);
        assertEquals(name, node.getName());
    }

    @Test
    void testGetAndSetChildren() {
        Node child1 = new Node(new ArrayList<>());
        Node child2 = new Node(new ArrayList<>());

        List<Node> newChildren = new ArrayList<>();
        newChildren.add(child1);
        newChildren.add(child2);

        node.setChildren(newChildren);
        assertEquals(2, node.getChildren().size());
        assertTrue(node.getChildren().contains(child1));
        assertTrue(node.getChildren().contains(child2));
    }

    @Test
    void testGetAndSetAttribute() {
        Collection<String> attributes = new ArrayList<>();
        attributes.add("attribute1");
        attributes.add("attribute2");

        node.setAttribute(attributes);
        assertEquals(2, node.getAttribute().size());
        assertTrue(node.getAttribute().contains("attribute1"));
        assertTrue(node.getAttribute().contains("attribute2"));
    }

    @Test
    void testGetAndSetMethod() {
        Collection<String> methods = new ArrayList<>();
        methods.add("method1()");
        methods.add("method2()");

        node.setMethod(methods);
        assertEquals(2, node.getMethod().size());
        assertTrue(node.getMethod().contains("method1()"));
        assertTrue(node.getMethod().contains("method2()"));
    }

    @Test
    void testInitialAttributesAreEmpty() {
        assertNotNull(node.getAttribute());
        assertEquals(0, node.getAttribute().size());
    }

    @Test
    void testInitialMethodsAreEmpty() {
        assertNotNull(node.getMethod());
        assertEquals(0, node.getMethod().size());
    }

    @Test
    void testToCSV() {
        // Test that method doesn't throw exception
        assertDoesNotThrow(() -> node.toCSV());
    }

    @Test
    void testComplexNodeStructure() {
        // Create a parent node
        Node parent = new Node(new ArrayList<>());
        parent.setName("Parent");

        Collection<String> parentAttrs = new ArrayList<>();
        parentAttrs.add("parentAttr");
        parent.setAttribute(parentAttrs);

        // Create children nodes
        Node child1 = new Node(new ArrayList<>());
        child1.setName("Child1");

        Node child2 = new Node(new ArrayList<>());
        child2.setName("Child2");

        List<Node> childrenList = new ArrayList<>();
        childrenList.add(child1);
        childrenList.add(child2);
        parent.setChildren(childrenList);

        // Assertions
        assertEquals("Parent", parent.getName());
        assertEquals(2, parent.getChildren().size());
        assertEquals("Child1", child1.getName());
        assertEquals("Child2", child2.getName());
    }
}
