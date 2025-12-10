package com.project.uml_project.ingeProjet.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PlantUMLReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testLireContenuPUMLWithValidFile() throws IOException {
        // Create a temporary file with content
        Path testFile = tempDir.resolve("test.puml");
        String content = "@startuml\nclass TestClass\n@enduml";
        Files.writeString(testFile, content);

        String result = PlantUMLReader.lireContenuPUML(testFile.toString());

        assertNotNull(result);
        assertEquals(content, result);
    }

    @Test
    void testLireContenuPUMLWithEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.puml");
        Files.writeString(testFile, "");

        String result = PlantUMLReader.lireContenuPUML(testFile.toString());

        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testLireContenuPUMLWithNonExistentFile() {
        String result = PlantUMLReader.lireContenuPUML("/non/existent/path/file.puml");

        assertNull(result);
    }

    @Test
    void testLireContenuPUMLWithMultilineContent() throws IOException {
        Path testFile = tempDir.resolve("multiline.puml");
        String content = "@startuml\n" +
                "class Person {\n" +
                "  +name: String\n" +
                "  +age: int\n" +
                "}\n" +
                "@enduml";
        Files.writeString(testFile, content);

        String result = PlantUMLReader.lireContenuPUML(testFile.toString());

        assertNotNull(result);
        assertTrue(result.contains("class Person"));
        assertTrue(result.contains("+name: String"));
    }

    @Test
    void testLireContenuPUMLWithSpecialCharacters() throws IOException {
        Path testFile = tempDir.resolve("special.puml");
        String content = "@startuml\nclass Café {\n  +prêt: boolean\n}\n@enduml";
        Files.writeString(testFile, content);

        String result = PlantUMLReader.lireContenuPUML(testFile.toString());

        assertNotNull(result);
        assertTrue(result.contains("Café"));
        assertTrue(result.contains("prêt"));
    }
}
