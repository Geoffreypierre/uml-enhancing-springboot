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

    public UMLEnhancer(Parser parser, FCA4JAdapter dca4jAdapter, EnhancedPumlBuilder pumlBuilder)
	{
		this.parser = parser;
		this.dca4jAdapter = dca4jAdapter;
		this.pumlBuilder = pumlBuilder;
    }

    // Charge les filtres, fichiers
    public void init(String pathToInputUml,float relevanceThreshold) throws Exception {
        //Récupère le PUML original dans le fichier
        originalUml = PlantUMLReader.lireContenuPUML(pathToInputUml);
        pumlBuilder.setLlmProvider(new LLMProvider(token, "gpt-4"));
        pumlBuilder.setFilterTreeshold(relevanceThreshold);
    };

    // Execute le processus
    public void exec() {
        // 1. Parser le diagramme UML
        Diagram originalDiagram = parser.parse(originalUml);
        // Diagrame -> KG avec diagram.toKnowledgeGraph()
        Node knowledgeGraph = originalDiagram.toKnowledgeGraph();
        // Trannsforme le KG en CSV
        knowledgeGraph.toCSV();
        // Appelle l'adapter avec le CSV pour générer les concepts FCA
        java.util.Collection<Concept> concepts = dca4jAdapter.generate();
        // Récupère les concepts et les passe au puml builder
        pumlBuilder.setConcepts(concepts);
        pumlBuilder.setOriginalDiagram(originalDiagram);
        // EnhancedPumlBuilder appelle le LLM pour améliorer le diagramme
        pumlBuilder.enhance();
        // Exporte le nouveau diagramme
        pumlBuilder.export();
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

	};


