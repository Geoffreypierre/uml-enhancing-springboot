package com.project.uml_project.ingeProjet.controller;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.uml_project.ingeProjet.fca4j.FCA4JAdapter;
import com.project.uml_project.ingeProjet.main.EnhancedPumlBuilder;
import com.project.uml_project.ingeProjet.main.UMLEnhancer;
import com.project.uml_project.ingeProjet.utils.Parser;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@RestController
@RequestMapping("/api/uml")
public class UmlController {

    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadPuml(
            @RequestParam("file") MultipartFile file,
            @RequestParam("relevance") float relevanceThreshold) throws Exception {

        var fileBytes = file.getBytes();
        String plantuml = new String(fileBytes, StandardCharsets.UTF_8);
        String newPlantUML = traits(plantuml, relevanceThreshold);

        return newPlantUML;

    }

    public String traits(String plantuml, float relevanceThreshold) throws Exception {

        Parser p = new Parser();
        FCA4JAdapter fc4JAdapter = new FCA4JAdapter();
        EnhancedPumlBuilder enhancedPumlBuilder = new EnhancedPumlBuilder();
        UMLEnhancer umlEnhancer = new UMLEnhancer(p, fc4JAdapter, enhancedPumlBuilder);

        System.out.println("Starting UML enhancement with relevance threshold: " + relevanceThreshold);
        umlEnhancer.init(plantuml, relevanceThreshold);
        System.out.println("UML content initialized successfully");

        umlEnhancer.exec();
        System.out.println("UML enhancement completed");

        return umlEnhancer.getResult();
    }

    @PostMapping(value = "/render", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> renderPuml(@RequestParam("puml") String pumlContent) {
        try {
            SourceStringReader reader = new SourceStringReader(pumlContent);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            reader.outputImage(os, new FileFormatOption(FileFormat.PNG));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl("no-cache");
            
            return new ResponseEntity<>(os.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error rendering PlantUML: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}