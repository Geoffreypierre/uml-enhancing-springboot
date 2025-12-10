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
        // Currently validate() always returns false
        parser.setPuml("@startuml\nclass Test\n@enduml");
        assertFalse(parser.validate());
    }

    @Test
    void testParseReturnsNullWhenValidationFails() {
        parser.setPuml("@startuml\nclass Test\n@enduml");

        Diagram result = parser.parse();

        // Since validate() returns false, parse() should return null
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
        assertDoesNotThrow(() -> parser.parse());
    }
}
