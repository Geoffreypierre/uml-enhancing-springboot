package com.project.uml_project.ingeProjet.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class PlantUMLReader {
    public static String lireContenuPUML(String cheminFichier) throws IOException {
        return Files.readString(Paths.get(cheminFichier));
    }
}
