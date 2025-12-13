package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.LLM.LLMProvider;
import com.project.uml_project.ingeProjet.utils.Diagram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class EnhancedPumlBuilderTest {

    @Mock
    private LLMProvider mockLlmProvider;

    @Mock
    private Diagram mockDiagram;

    private EnhancedPumlBuilder builder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        builder = new EnhancedPumlBuilder();
    }

    @Test
    void testConstructor() {
        assertNotNull(builder);
    }

    @Test
    void testGetAndSetConcepts() {
        Collection<Concept> concepts = new ArrayList<>();
        Concept concept1 = new Concept("Orig1", null, new ArrayList<>(), new ArrayList<>(), "Name1");
        concepts.add(concept1);

        builder.setConcepts(concepts);

        assertEquals(1, builder.getConcepts().size());
        assertTrue(builder.getConcepts().contains(concept1));
    }

    @Test
    void testGetAndSetOriginalDiagram() {
        builder.setOriginalDiagram(mockDiagram);

        assertEquals(mockDiagram, builder.getOriginalDiagram());
    }

    @Test
    void testGetAndSetFilterTreeshold() {
        float threshold = 0.75f;
        builder.setFilterTreeshold(threshold);

        assertEquals(threshold, builder.getFilterTreeshold(), 0.001);
    }

    @Test
    void testGetAndSetLlmProvider() {
        builder.setLlmProvider(mockLlmProvider);

        assertEquals(mockLlmProvider, builder.getLlmProvider());
    }

    @Test
    void testEnhanceWithEmptyConcepts() {
        builder.setConcepts(new ArrayList<>());

        assertDoesNotThrow(() -> builder.enhance());
    }

    @Test
    void testEnhanceFiltersConcepts() throws Exception {
        when(mockLlmProvider.request(anyString())).thenReturn("0.5");

        Collection<Concept> concepts = new ArrayList<>();
        Concept concept1 = new Concept("Orig1", mockLlmProvider, new ArrayList<>(), new ArrayList<>(), "Name1");
        Concept concept2 = new Concept("Orig2", mockLlmProvider, new ArrayList<>(), new ArrayList<>(), "Name2");
        concepts.add(concept1);
        concepts.add(concept2);

        builder.setConcepts(concepts);
        builder.setLlmProvider(mockLlmProvider);
        builder.setFilterTreeshold(0.6f);

        builder.enhance();

        // Concepts with score < 0.6 should be filtered out
        assertEquals(0, builder.getConcepts().size());
    }

    @Test
    void testEnhanceKeepsHighScoredConcepts() throws Exception {
        when(mockLlmProvider.request(anyString())).thenReturn("0.8");

        Collection<Concept> concepts = new ArrayList<>();
        Concept concept1 = new Concept("Orig1", mockLlmProvider, new ArrayList<>(), new ArrayList<>(), "Name1");
        concepts.add(concept1);

        builder.setConcepts(concepts);
        builder.setLlmProvider(mockLlmProvider);
        builder.setFilterTreeshold(0.6f);

        builder.enhance();

        // Concepts with score >= 0.6 should be kept
        assertEquals(1, builder.getConcepts().size());
    }

    @Test
    void testExport() {
        // Test that export doesn't throw exception
        assertDoesNotThrow(() -> builder.export());
    }

    @Test
    void testEnhanceWithNullConcepts() {
        builder.setConcepts(null);

        // Should not throw an exception, but handle gracefully
        assertDoesNotThrow(() -> builder.enhance());
    }
}
