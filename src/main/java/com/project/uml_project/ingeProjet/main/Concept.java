package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.LLM.LLMProvider;

import java.util.Optional;

public class Concept {
    private static final String requeteString = "";
    private String originalName;
    private java.util.Collection<String> attribute;
    private java.util.Collection<String> method;
    private String name;
    private Optional<Float> relevanceScore;
    private LLMProvider llmProvider;

    public Concept(String originalName,
                   LLMProvider llmProvider,
                   java.util.Collection<String> attribute, java.util.Collection<String> method, String name)
	{
        this.llmProvider = llmProvider;
		this.originalName = originalName;
		this.attribute = attribute;
		this.method = method;
		this.name = name;

    }

    public float relevanceScore() {
        //Appeler le llm provider
        if (relevanceScore.isEmpty()) {
            String stringScore = llmProvider.request(requeteString);
            relevanceScore = Optional.of(Float.parseFloat(stringScore));
        }
        return relevanceScore.get();
    }
    public String toPuml() {return null;};

    public String getOriginalName() {
        return this.originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    public java.util.Collection<String> getAttribute() {
        return this.attribute;
    }

    public void setAttribute(java.util.Collection<String> attribute) {
        this.attribute = attribute;
    }
    public java.util.Collection<String> getMethod() {
        return this.method;
    }

    public void setMethod(java.util.Collection<String> method) {
        this.method = method;
    }
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

	};


