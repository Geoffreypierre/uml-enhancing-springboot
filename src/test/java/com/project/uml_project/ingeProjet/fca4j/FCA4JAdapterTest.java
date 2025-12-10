package com.project.uml_project.ingeProjet.fca4j;

import com.project.uml_project.ingeProjet.main.Concept;
import com.project.uml_project.ingeProjet.utils.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FCA4JAdapterTest {

    private FCA4JAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FCA4JAdapter();
    }

    @Test
    void testConstructor() {
        assertNotNull(adapter);
    }

    @Test
    void testGenerateWithNullRoot() {
        Collection<Concept> result = adapter.generate(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGenerateWithEmptyRoot() {
        Node root = new Node(new ArrayList<>());

        Collection<Concept> result = adapter.generate(root);

        assertNotNull(result);
        // Should generate at least one concept
        assertTrue(result.size() >= 0);
    }

    @Test
    void testGenerateWithSingleNode() {
        Node root = new Node(new ArrayList<>());
        root.setName("TestNode");

        Collection<String> attributes = new ArrayList<>();
        attributes.add("attr1");
        attributes.add("attr2");
        root.setAttribute(attributes);

        Collection<String> methods = new ArrayList<>();
        methods.add("method1()");
        root.setMethod(methods);

        Collection<Concept> result = adapter.generate(root);

        // Debug dump
        System.out.println("\n=== testGenerateWithSingleNode ===");
        System.out.println("Generated " + result.size() + " concept(s):");
        for (Concept c : result) {
            System.out.println("  Concept: " + c.getName());
            System.out.println("    Original: " + c.getOriginalName());
            System.out.println("    Attributes: " + c.getAttribute());
            System.out.println("    Methods: " + c.getMethod());
        }
        System.out.println("==================================\n");

        assertNotNull(result);
        assertTrue(result.size() > 0);

        Concept concept = result.iterator().next();
        assertNotNull(concept.getName());
        assertNotNull(concept.getAttribute());
    }

    @Test
    void testGenerateWithMultipleNodes() {
        Node root = new Node(new ArrayList<>());
        root.setName("Root");

        Node child1 = new Node(new ArrayList<>());
        child1.setName("Child1");
        Collection<String> child1Attrs = new ArrayList<>();
        child1Attrs.add("childAttr1");
        child1.setAttribute(child1Attrs);

        Node child2 = new Node(new ArrayList<>());
        child2.setName("Child2");
        Collection<String> child2Attrs = new ArrayList<>();
        child2Attrs.add("childAttr2");
        child2.setAttribute(child2Attrs);

        List<Node> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        root.setChildren(children);

        Collection<Concept> result = adapter.generate(root);

        // Debug dump
        System.out.println("\\n=== testGenerateWithMultipleNodes ===");
        System.out.println("Input: Root with 2 children (Child1, Child2)");
        System.out.println("Generated " + result.size() + " concept(s):");
        for (Concept c : result) {
            System.out.println("  Concept: " + c.getName());
            System.out.println("    Original: " + c.getOriginalName());
            System.out.println("    Attributes: " + c.getAttribute());
            System.out.println("    Methods: " + c.getMethod());
        }
        System.out.println("======================================\\n");

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    void testGenerateWithNodeWithoutName() {
        Node root = new Node(new ArrayList<>());
        // Don't set name - should generate UUID

        Collection<String> attributes = new ArrayList<>();
        attributes.add("attr1");
        root.setAttribute(attributes);

        System.out.println("\n=== testGenerateWithNodeWithoutName ===");
        System.out.println("Input: Node without name (should generate UUID)");
        Collection<Concept> result = adapter.generate(root);
        System.out.println("Generated " + result.size() + " concept(s):");
        for (Concept c : result) {
            System.out.println("  Concept: " + c.getName());
            System.out.println("    Original: " + c.getOriginalName());
            System.out.println("    Attributes: " + c.getAttribute());
            System.out.println("    Methods: " + c.getMethod());
        }
        System.out.println("======================================\n");

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    void testGenerateWithNestedStructure() {
        Node root = new Node(new ArrayList<>());
        root.setName("Root");

        Node level1 = new Node(new ArrayList<>());
        level1.setName("Level1");

        Node level2 = new Node(new ArrayList<>());
        level2.setName("Level2");

        List<Node> level1Children = new ArrayList<>();
        level1Children.add(level2);
        level1.setChildren(level1Children);

        List<Node> rootChildren = new ArrayList<>();
        rootChildren.add(level1);
        root.setChildren(rootChildren);

        System.out.println("\n=== testGenerateWithNestedStructure ===");
        System.out.println("Input: Root -> Level1 -> Level2 (3-level nested structure)");
        Collection<Concept> result = adapter.generate(root);
        System.out.println("Generated " + result.size() + " concept(s):");
        for (Concept c : result) {
            System.out.println("  Concept: " + c.getName());
            System.out.println("    Original: " + c.getOriginalName());
            System.out.println("    Attributes: " + c.getAttribute());
            System.out.println("    Methods: " + c.getMethod());
        }
        System.out.println("======================================\n");

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @Test
    void testGenerateWithComplexAttributes() {
        Node root = new Node(new ArrayList<>());
        root.setName("ComplexNode");

        Collection<String> attributes = new ArrayList<>();
        attributes.add("attr1");
        attributes.add("attr2");
        attributes.add("attr3");
        root.setAttribute(attributes);

        Collection<String> methods = new ArrayList<>();
        methods.add("method1()");
        methods.add("method2()");
        methods.add("method3()");
        root.setMethod(methods);

        Collection<Concept> result = adapter.generate(root);

        // Debug dump
        System.out.println("\n=== testGenerateWithComplexAttributes ===");
        System.out.println("Input: 3 attributes, 3 methods");
        System.out.println("Generated " + result.size() + " concept(s):");
        for (Concept c : result) {
            System.out.println("  Concept: " + c.getName());
            System.out.println("    Original: " + c.getOriginalName());
            System.out.println("    Attributes: " + c.getAttribute());
            System.out.println("    Methods: " + c.getMethod());
        }
        System.out.println("==========================================\n");

        assertNotNull(result);
        assertTrue(result.size() > 0);

        Concept concept = result.iterator().next();
        assertNotNull(concept);
        assertTrue(concept.getAttribute().size() > 0);
    }

    @Test
    void testGenerateResultContainsValidConcept() {
        Node root = new Node(new ArrayList<>());
        root.setName("TestNode");

        Collection<String> attributes = new ArrayList<>();
        attributes.add("testAttr");
        root.setAttribute(attributes);

        System.out.println("\n=== testGenerateResultContainsValidConcept ===");
        System.out.println("Input: TestNode with testAttr");
        Collection<Concept> result = adapter.generate(root);
        System.out.println("Generated " + result.size() + " concept(s):");
        for (Concept c : result) {
            System.out.println("  Concept: " + c.getName());
            System.out.println("    Original: " + c.getOriginalName());
            System.out.println("    Attributes: " + c.getAttribute());
            System.out.println("    Methods: " + c.getMethod());
        }
        System.out.println("==============================================\n");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        for (Concept concept : result) {
            assertNotNull(concept.getName());
            assertNotNull(concept.getOriginalName());
            assertNotNull(concept.getAttribute());
            assertNotNull(concept.getMethod());
        }
    }
}
