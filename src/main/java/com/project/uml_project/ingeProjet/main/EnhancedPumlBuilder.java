package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.utils.Diagram;
import com.project.uml_project.ingeProjet.LLM.LLMProvider;

public class EnhancedPumlBuilder {

    private java.util.Collection<Concept> concepts;
    private Diagram originalDiagram;
    private float filterTreeshold;
    private LLMProvider llmProvider;

    public EnhancedPumlBuilder()
	{
    }

    // Doit améliorer l'uml original
    public void enhance() {
        // Appelle le LLM pour noter les concepts et les nommer
        for (Concept concept : concepts){
            concept.relevanceScore();
            concept.setNameFromLLM();
        }

        // Filtre les concepts avec le treshold
        concepts.removeIf(concept ->
                concept.relevanceScore() < filterTreeshold);

    }

    // exporte le nouveau diagramme en puml
    public void export() {

    };


    public java.util.Collection<Concept> getConcepts() {
        return this.concepts;
    }

    public void setConcepts(java.util.Collection<Concept> concepts) {
        this.concepts = concepts;
    }
    public Diagram getOriginalDiagram() {
        return this.originalDiagram;
    }

    public void setOriginalDiagram(Diagram originalDiagram) {
        this.originalDiagram = originalDiagram;
    }
    public float getFilterTreeshold() {
        return this.filterTreeshold;
    }

    public void setFilterTreeshold(float filterTreeshold) {
        this.filterTreeshold = filterTreeshold;
    }
    public LLMProvider getLlmProvider() {
        return this.llmProvider;
    }

    public void setLlmProvider(LLMProvider llmProvider) {
        this.llmProvider = llmProvider;
    }

	};


