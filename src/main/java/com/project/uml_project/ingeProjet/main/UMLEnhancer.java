package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.LLM.LLMProvider;
import com.project.uml_project.ingeProjet.fca4j.FCA4JAdapter;
import com.project.uml_project.ingeProjet.utils.Diagram;
import com.project.uml_project.ingeProjet.utils.Node;
import com.project.uml_project.ingeProjet.utils.Parser;
import com.project.uml_project.ingeProjet.utils.PlantUMLReader;

public class UMLEnhancer {
    private String token;
    private Parser parser;
    private FCA4JAdapter dca4jAdapter;
    private EnhancedPumlBuilder pumlBuilder;
    private String originalUml;
    private String result;

    public UMLEnhancer(Parser parser, FCA4JAdapter dca4jAdapter, EnhancedPumlBuilder pumlBuilder) {
        this.parser = parser;
        this.dca4jAdapter = dca4jAdapter;
        this.pumlBuilder = pumlBuilder;
    }

    // Charge les filtres, fichiers
    public void init(String pathToInputUml, float relevanceThreshold) throws Exception {
        // Récupère le PUML original dans le fichier
        originalUml = PlantUMLReader.lireContenuPUML(pathToInputUml);

        if (originalUml == null || originalUml.isEmpty()) {
            throw new Exception("Failed to read PUML file or file is empty: " + pathToInputUml);
        }

        parser.setPuml(originalUml);

        // Use environment variable for token if not already set
        if (token == null || token.isEmpty()) {
            token = System.getenv("OPENAI_API_KEY");
        }

        pumlBuilder.setLlmProvider(new LLMProvider(token, "gpt-4o-mini"));
        pumlBuilder.setFilterTreeshold(relevanceThreshold);
    };

    // Execute le processus
    public void exec() throws Exception {
        // 1. Parser le diagramme UML

        Diagram originalDiagram = parser.parse();
        // Diagrame -> KG avec diagram.toKnowledgeGraph()
        Node knowledgeGraph = originalDiagram.toKnowledgeGraph();
        // Trannsforme le KG en CSV
        knowledgeGraph.toCSV();
        // Appelle l'adapter avec le CSV pour générer les concepts FCA
        java.util.Collection<Concept> concepts = dca4jAdapter.generate(knowledgeGraph);
        // Récupère les concepts et les passe au puml builder
        pumlBuilder.setConcepts(concepts);
        pumlBuilder.setOriginalDiagram(originalDiagram);
        // EnhancedPumlBuilder appelle le LLM pour améliorer le diagramme
        pumlBuilder.enhance();
        // Exporte le nouveau diagramme
        result = pumlBuilder.export();
    };

    public Parser getParser() {
        return this.parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public FCA4JAdapter getDca4jAdapter() {
        return this.dca4jAdapter;
    }

    public void setDca4jAdapter(FCA4JAdapter dca4jAdapter) {
        this.dca4jAdapter = dca4jAdapter;
    }

    public EnhancedPumlBuilder getPumlBuilder() {
        return this.pumlBuilder;
    }

    public void setPumlBuilder(EnhancedPumlBuilder pumlBuilder) {
        this.pumlBuilder = pumlBuilder;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResult() {
        return this.result;
    }

};
