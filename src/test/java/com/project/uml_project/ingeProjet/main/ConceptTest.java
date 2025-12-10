package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.LLM.LLMProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ConceptTest {

    @Mock
    private LLMProvider mockLlmProvider;

    private Concept concept;
    private Collection<String> attributes;
    private Collection<String> methods;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        attributes = new ArrayList<>();
        attributes.add("attribute1");
        attributes.add("attribute2");

        methods = new ArrayList<>();
        methods.add("method1()");
        methods.add("method2()");

        concept = new Concept("OriginalName", null, attributes, methods, "ConceptName");
    }

    @Test
    void testConstructor() {
        assertNotNull(concept);
        assertEquals("OriginalName", concept.getOriginalName());
        assertEquals("ConceptName", concept.getName());
        assertEquals(2, concept.getAttribute().size());
        assertEquals(2, concept.getMethod().size());
    }

    @Test
    void testGetAndSetOriginalName() {
        concept.setOriginalName("NewOriginalName");
        assertEquals("NewOriginalName", concept.getOriginalName());
    }

    @Test
    void testGetAndSetName() {
        concept.setName("NewName");
        assertEquals("NewName", concept.getName());
    }

    @Test
    void testGetAndSetAttribute() {
        Collection<String> newAttributes = new ArrayList<>();
        newAttributes.add("newAttr");

        concept.setAttribute(newAttributes);
        assertEquals(1, concept.getAttribute().size());
        assertTrue(concept.getAttribute().contains("newAttr"));
    }

    @Test
    void testGetAndSetMethod() {
        Collection<String> newMethods = new ArrayList<>();
        newMethods.add("newMethod()");

        concept.setMethod(newMethods);
        assertEquals(1, concept.getMethod().size());
        assertTrue(concept.getMethod().contains("newMethod()"));
    }

    @Test
    void testToPuml() {
        String puml = concept.toPuml();

        assertNotNull(puml);
        assertTrue(puml.contains("abstract class \"ConceptName\""));
        assertTrue(puml.contains("{field}"));
        assertTrue(puml.contains("{method}"));
        assertTrue(puml.contains("attribute1"));
        assertTrue(puml.contains("method1()"));
    }

    @Test
    void testToPumlWithNullAttributes() {
        Concept conceptWithNullAttrs = new Concept("Test", null, null, methods, "TestName");
        String puml = conceptWithNullAttrs.toPuml();

        assertNotNull(puml);
        assertTrue(puml.contains("abstract class \"TestName\""));
    }

    @Test
    void testToPumlWithNullMethods() {
        Concept conceptWithNullMethods = new Concept("Test", null, attributes, null, "TestName");
        String puml = conceptWithNullMethods.toPuml();

        assertNotNull(puml);
        assertTrue(puml.contains("abstract class \"TestName\""));
    }

    @Test
    void testToPumlWithEmptyAttributesAndMethods() {
        Concept emptyConcept = new Concept("Test", null, new ArrayList<>(), new ArrayList<>(), "EmptyClass");
        String puml = emptyConcept.toPuml();

        assertNotNull(puml);
        assertTrue(puml.contains("abstract class \"EmptyClass\""));
        assertTrue(puml.contains("{"));
        assertTrue(puml.contains("}"));
    }

    @Test
    void testSetNameFromLLMWithMockProvider() {
        when(mockLlmProvider.request(anyString())).thenReturn("LLMGeneratedName");

        Concept conceptWithLLM = new Concept("Original", mockLlmProvider, attributes, methods, "Initial");
        String result = conceptWithLLM.setNameFromLLM();

        assertEquals("LLMGeneratedName", result);
        assertEquals("LLMGeneratedName", conceptWithLLM.getName());
    }

    @Test
    void testRelevanceScoreWithMockProvider() {
        when(mockLlmProvider.request(anyString())).thenReturn("0.85");

        Concept conceptWithLLM = new Concept("Original", mockLlmProvider, attributes, methods, "Initial");
        float score = conceptWithLLM.relevanceScore();

        assertEquals(0.85f, score, 0.001);
    }

    @Test
    void testRelevanceScoreCaching() {
        when(mockLlmProvider.request(anyString())).thenReturn("0.75");

        Concept conceptWithLLM = new Concept("Original", mockLlmProvider, attributes, methods, "Initial");

        float score1 = conceptWithLLM.relevanceScore();
        float score2 = conceptWithLLM.relevanceScore();

        assertEquals(score1, score2);
    }
}
