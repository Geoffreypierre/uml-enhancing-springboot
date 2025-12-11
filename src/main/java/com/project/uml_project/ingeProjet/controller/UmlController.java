package com.project.uml_project.ingeProjet.controller;

import com.project.uml_project.ingeProjet.fca4j.FCA4JAdapter;
import com.project.uml_project.ingeProjet.main.EnhancedPumlBuilder;
import com.project.uml_project.ingeProjet.main.UMLEnhancer;
import com.project.uml_project.ingeProjet.utils.Parser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/uml")
public class UmlController {

    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadPuml(
            @RequestParam("file") MultipartFile file,
            @RequestParam("relevance") float relevanceThreshold
    ) {
        try {
            String plantuml = new String(file.getBytes(), StandardCharsets.UTF_8);
            String newPlantUML = traits(plantuml, relevanceThreshold);

            return newPlantUML;

        } catch (Exception e) {
            return "Erreur lors de l'enregistrement du fichier : " + e.getMessage();
        }
    }

    public String traits(String plantuml, float relevanceThreshold) throws Exception {

        Parser p = new Parser();
        FCA4JAdapter fc4JAdapter = new FCA4JAdapter();
        EnhancedPumlBuilder enhancedPumlBuilder = new EnhancedPumlBuilder();
        UMLEnhancer umlEnhancer = new UMLEnhancer(p, fc4JAdapter, enhancedPumlBuilder);

        umlEnhancer.init(plantuml, relevanceThreshold);
        umlEnhancer.exec();
        return umlEnhancer.getResult();
    }


}