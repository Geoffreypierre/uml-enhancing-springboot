package com.project.uml_project.ingeProjet.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/uml")
public class UmlController {

    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public String uploadPuml(@RequestParam("file") MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Erreur lors de l'enregistrement du fichier : " + e.getMessage();
        }
    }
}