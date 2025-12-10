package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.fca4j.FCA4JAdapter;
import com.project.uml_project.ingeProjet.utils.Parser;
import com.project.uml_project.ingeProjet.utils.Diagram;
import com.project.uml_project.ingeProjet.utils.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UMLEnhancerTest {

    @Mock
    private Parser mockParser;

    @Mock
    private FCA4JAdapter mockFca4jAdapter;

    @Mock
    private EnhancedPumlBuilder mockPumlBuilder;

    @Mock
    private Diagram mockDiagram;

    @Mock
    private Node mockNode;

    private UMLEnhancer enhancer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        enhancer = new UMLEnhancer(mockParser, mockFca4jAdapter, mockPumlBuilder);
    }

    @Test
    void testConstructor() {
        assertNotNull(enhancer);
        assertEquals(mockParser, enhancer.getParser());
        assertEquals(mockFca4jAdapter, enhancer.getDca4jAdapter());
        assertEquals(mockPumlBuilder, enhancer.getPumlBuilder());
    }

    @Test
    void testGetAndSetParser() {
        Parser newParser = new Parser();
        enhancer.setParser(newParser);

        assertEquals(newParser, enhancer.getParser());
    }

    @Test
    void testGetAndSetDca4jAdapter() {
        FCA4JAdapter newAdapter = new FCA4JAdapter();
        enhancer.setDca4jAdapter(newAdapter);

        assertEquals(newAdapter, enhancer.getDca4jAdapter());
    }

    @Test
    void testGetAndSetPumlBuilder() {
        EnhancedPumlBuilder newBuilder = new EnhancedPumlBuilder();
        enhancer.setPumlBuilder(newBuilder);

        assertEquals(newBuilder, enhancer.getPumlBuilder());
    }

    @Test
    void testInitWithValidFile() throws Exception {
        Path testFile = tempDir.resolve("test.puml");
        String content = "@startuml\nclass TestClass\n@enduml";
        Files.writeString(testFile, content);

        // Note: This will fail if LLMProvider requires valid token
        // For now, we expect it to throw when setting up LLMProvider
        assertThrows(Exception.class, () -> enhancer.init(testFile.toString(), 0.5f));
    }

    @Test
    void testInitWithInvalidFile() {
        assertThrows(Exception.class,
                () -> enhancer.init("/non/existent/file.puml", 0.5f));
    }

    @Test
    void testExecFlow() {
        // Setup mocks
        when(mockParser.parse()).thenReturn(mockDiagram);
        when(mockDiagram.toKnowledgeGraph()).thenReturn(mockNode);

        Collection<Concept> concepts = new ArrayList<>();
        Concept concept = new Concept("Test", null, new ArrayList<>(), new ArrayList<>(), "TestConcept");
        concepts.add(concept);
        when(mockFca4jAdapter.generate(any(Node.class))).thenReturn(concepts);

        // Execute
        assertDoesNotThrow(() -> enhancer.exec());

        // Verify the flow
        verify(mockParser, times(1)).parse();
        verify(mockDiagram, times(1)).toKnowledgeGraph();
        verify(mockNode, times(1)).toCSV();
        verify(mockFca4jAdapter, times(1)).generate(any(Node.class));
        verify(mockPumlBuilder, times(1)).setConcepts(any());
        verify(mockPumlBuilder, times(1)).setOriginalDiagram(any());
        verify(mockPumlBuilder, times(1)).enhance();
        verify(mockPumlBuilder, times(1)).export();
    }

    @Test
    void testExecWithNullDiagram() {
        when(mockParser.parse()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> enhancer.exec());
    }

    @Test
    void testExecWithNullKnowledgeGraph() {
        when(mockParser.parse()).thenReturn(mockDiagram);
        when(mockDiagram.toKnowledgeGraph()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> enhancer.exec());
    }
}
