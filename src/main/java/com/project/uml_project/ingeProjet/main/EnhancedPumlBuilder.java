package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.utils.Diagram;
import com.project.uml_project.ingeProjet.LLM.LLMMProvider;
import com.project.uml_project.ingeProjet.main.Concept;






public class EnhancedPumlBuilder {

    private java.util.Collection<Concept> concepts;
    private Diagram originalDiagram;
    private float filterTreeshold;
    private LLMMProvider llmProvider;

    public EnhancedPumlBuilder(java.util.Collection<Concept> concepts, Diagram originalDiagram, float filterTreeshold, LLMMProvider llmProvider) 
	{
		this.concepts = concepts;
		this.originalDiagram = originalDiagram;
		this.filterTreeshold = filterTreeshold;
		this.llmProvider = llmProvider;
    }

    public void enhance() {};
    public void export() {};


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
    public LLMMProvider getLlmProvider() {
        return this.llmProvider;
    }

    public void setLlmProvider(LLMMProvider llmProvider) {
        this.llmProvider = llmProvider;
    }

	};


