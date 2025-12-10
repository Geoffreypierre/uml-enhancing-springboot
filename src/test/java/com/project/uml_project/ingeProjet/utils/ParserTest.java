package com.project.uml_project.ingeProjet.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void testConstructor() {
        assertNotNull(parser);
    }

    @Test
    void testSetPuml() {
        String puml = "@startuml\nclass Test\n@enduml";
        assertDoesNotThrow(() -> parser.setPuml(puml));
    }

    @Test
    void testValidateReturnsFalse() {
        // Test with invalid/empty PUML
        parser.setPuml("");
        assertFalse(parser.validate());

        parser.setPuml(null);
        assertFalse(parser.validate());
    }

    @Test
    void testParseReturnsNullWhenValidationFails() {
        // Empty PUML should fail validation
        parser.setPuml("");

        Diagram result = parser.parse();

        // Since validate() returns false for empty PUML, parse() should return null
        assertNull(result);
    }

    @Test
    void testParseWithNullPuml() {
        parser.setPuml(null);

        Diagram result = parser.parse();

        assertNull(result);
    }

    @Test
    void testParseWithEmptyPuml() {
        parser.setPuml("");

        Diagram result = parser.parse();

        assertNull(result);
    }

    @Test
    void testSetPumlMultipleTimes() {
        parser.setPuml("@startuml\nclass Test1\n@enduml");
        parser.setPuml("@startuml\nclass Test2\n@enduml");

        // Should not throw exception
        assertDoesNotThrow(() -> parser.setPuml("@startuml\nclass Test3\n@enduml"));
    }

    @Test
    void testValidateReturnsTrue() {
        // Test with valid PUML
        System.setProperty("java.awt.headless", "true");
        parser.setPuml("@startuml\nclass Test\n@enduml");
        assertTrue(parser.validate());
    }

    @Test
    void testParseWithValidPuml() {
        // Test that valid PUML returns a Diagram
        System.setProperty("java.awt.headless", "true");
        parser.setPuml("@startuml\nclass Person\n@enduml");

        Diagram result = parser.parse();

        assertNotNull(result);
    }
}
