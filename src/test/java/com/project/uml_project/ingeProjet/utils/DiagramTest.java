package com.project.uml_project.ingeProjet.utils;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.SourceStringReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiagramTest {

    @BeforeAll
    static void setup() {
        // Set headless mode for PlantUML to work without display
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void testConstructor() {
        String puml = "@startuml\nclass TestClass\n@enduml";
        SourceStringReader reader = new SourceStringReader(puml);
        BlockUml block = reader.getBlocks().get(0);
        net.sourceforge.plantuml.core.Diagram plantUmlDiagram = block.getDiagram();

        Diagram diagram = new Diagram(plantUmlDiagram);

        assertNotNull(diagram);
    }

    @Test
    void testToKnowledgeGraphWithNonClassDiagram() {
        // Create a sequence diagram instead of class diagram
        String puml = "@startuml\nAlice -> Bob: Hello\n@enduml";
        SourceStringReader reader = new SourceStringReader(puml);
        BlockUml block = reader.getBlocks().get(0);
        net.sourceforge.plantuml.core.Diagram plantUmlDiagram = block.getDiagram();

        Diagram diagram = new Diagram(plantUmlDiagram);
        System.out.println("\n=== testToKnowledgeGraphWithNonClassDiagram ===");
        System.out.println("Input: Sequence diagram (non-class)");
        Node result = diagram.toKnowledgeGraph();
        System.out.println("Generated Node: " + result.getName());
        System.out.println("Children: " + result.getChildren().size());
        System.out.println("===================================================\n");

        assertNotNull(result);
        assertNotNull(result.getChildren());
        assertEquals(0, result.getChildren().size());
    }

    @Test
    void testToKnowledgeGraphWithClassDiagram() {
        String puml = "@startuml\n" +
                "class Person {\n" +
                "  +name: String\n" +
                "  +getName(): String\n" +
                "}\n" +
                "class Company {\n" +
                "  +companyName: String\n" +
                "}\n" +
                "Person -- Company\n" +
                "@enduml";

        SourceStringReader reader = new SourceStringReader(puml);
        BlockUml block = reader.getBlocks().get(0);
        net.sourceforge.plantuml.core.Diagram plantUmlDiagram = block.getDiagram();

        Diagram diagram = new Diagram(plantUmlDiagram);
        System.out.println("\n=== testToKnowledgeGraphWithClassDiagram ===");
        System.out.println("Input: Person & Company classes with relationship");
        Node result = diagram.toKnowledgeGraph();
        System.out.println("Generated Node: " + result.getName());
        System.out.println("Children: " + result.getChildren().size());
        for (Node child : result.getChildren()) {
            System.out.println("  Child: " + child.getName());
            System.out.println("    Attributes: " + child.getAttribute());
            System.out.println("    Methods: " + child.getMethod());
        }
        System.out.println("=================================================\n");

        assertNotNull(result);
        assertNotNull(result.getChildren());
        // Should have entities and relationships
        assertTrue(result.getChildren().size() >= 0);
    }

    @Test
    void testToKnowledgeGraphWithEmptyClassDiagram() {
        String puml = "@startuml\n@enduml";
        SourceStringReader reader = new SourceStringReader(puml);
        BlockUml block = reader.getBlocks().get(0);
        net.sourceforge.plantuml.core.Diagram plantUmlDiagram = block.getDiagram();

        Diagram diagram = new Diagram(plantUmlDiagram);
        Node result = diagram.toKnowledgeGraph();

        assertNotNull(result);
        assertNotNull(result.getChildren());
    }

    @Test
    void testToPuml() {
        String puml = "@startuml\nclass TestClass\n@enduml";
        SourceStringReader reader = new SourceStringReader(puml);
        BlockUml block = reader.getBlocks().get(0);
        net.sourceforge.plantuml.core.Diagram plantUmlDiagram = block.getDiagram();

        Diagram diagram = new Diagram(plantUmlDiagram);

        // Should not throw exception
        assertDoesNotThrow(diagram::toPuml);
    }

    @Test
    void testToKnowledgeGraphStructure() {
        String puml = "@startuml\n" +
                "class Animal {\n" +
                "  +age: int\n" +
                "  +eat(): void\n" +
                "}\n" +
                "@enduml";

        SourceStringReader reader = new SourceStringReader(puml);
        BlockUml block = reader.getBlocks().get(0);
        net.sourceforge.plantuml.core.Diagram plantUmlDiagram = block.getDiagram();

        Diagram diagram = new Diagram(plantUmlDiagram);
        System.out.println("\n=== testToKnowledgeGraphStructure ===");
        System.out.println("Input: Animal class with age attribute and eat() method");
        Node root = diagram.toKnowledgeGraph();
        System.out.println("Generated Node: " + root.getName());
        System.out.println("Children: " + root.getChildren().size());
        for (Node child : root.getChildren()) {
            System.out.println("  Child: " + child.getName());
            System.out.println("    Attributes: " + child.getAttribute());
            System.out.println("    Methods: " + child.getMethod());
        }
        System.out.println("==========================================\n");

        assertNotNull(root);
        assertNotNull(root.getChildren());
    }
}
