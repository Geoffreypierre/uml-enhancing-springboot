package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.LLM.LLMProvider;

import java.util.Optional;

public class Concept {
    private static final String PROMPT_TEMPLATE_SCORE = "Analyze this UML concept and rate its relevance for a well-designed class diagram.\n"
            +
            "Concept Name: %s\n" +
            "Attributes: %s\n" +
            "Methods: %s\n\n" +
            "Rate the relevance on a scale from 0.0 to 1.0 where:\n" +
            "- 1.0 = Highly relevant, well-defined, cohesive concept\n" +
            "- 0.5 = Moderately relevant, could be improved\n" +
            "- 0.0 = Not relevant, poorly defined\n\n" +
            "Respond with ONLY a number between 0.0 and 1.0, nothing else.";

    private static final String PROMPT_TEMPLATE_NAME = "Given this UML concept, suggest a better, more descriptive class name.\n"
            +
            "Original Name: %s\n" +
            "Attributes: %s\n" +
            "Methods: %s\n\n" +
            "Respond with ONLY the suggested class name, nothing else. " +
            "Use PascalCase (e.g., UserAccount, OrderManager).";

    private String originalName;
    private java.util.Collection<String> attribute;
    private java.util.Collection<String> method;
    private String name;
    private Optional<Float> relevanceScore;
    private LLMProvider llmProvider;

    public Concept(String originalName,
            LLMProvider llmProvider,
            java.util.Collection<String> attribute, java.util.Collection<String> method, String name) {
        this.llmProvider = llmProvider;
        this.originalName = originalName;
        this.attribute = attribute;
        this.method = method;
        this.name = name;
        this.relevanceScore = Optional.empty();
    }

    public float relevanceScore() throws Exception {
        // Call the LLM provider to score this concept
        if (relevanceScore.isEmpty()) {
            // Build prompt with concept details
            String prompt = String.format(PROMPT_TEMPLATE_SCORE,
                    this.originalName != null ? this.originalName : "Unknown",
                    this.attribute != null ? this.attribute.toString() : "[]",
                    this.method != null ? this.method.toString() : "[]");

            String stringScore = llmProvider.request(prompt);

            // Parse the score, handling potential formatting issues
            stringScore = stringScore.trim().replaceAll("[^0-9.]", "");
            float score = Float.parseFloat(stringScore);

            // Clamp to valid range [0.0, 1.0]
            score = Math.max(0.0f, Math.min(1.0f, score));

            relevanceScore = Optional.of(score);
        }
        return relevanceScore.get();
    }

    public String setNameFromLLM() throws Exception {
        // Build prompt with concept details
        String prompt = String.format(PROMPT_TEMPLATE_NAME,
                this.originalName != null ? this.originalName : "Unknown",
                this.attribute != null ? this.attribute.toString() : "[]",
                this.method != null ? this.method.toString() : "[]");

        String suggestedName = llmProvider.request(prompt);

        // Clean up the response (remove quotes, trim, take first word if multiple)
        suggestedName = suggestedName.trim()
                .replaceAll("^\"|\"$", "") // Remove surrounding quotes
                .replaceAll("^'|'$", "") // Remove surrounding single quotes
                .split("\\s+")[0]; // Take first word if multiple

        this.name = suggestedName;
        return this.name;
    }

    // Probleme avec le PUML
    public String toPuml() {
        StringBuilder puml = new StringBuilder();
        puml.append("abstract class \"").append(this.name).append("\" {\n");

        if (this.attribute != null) {
            for (String attr : this.attribute) {
                puml.append("  {field} ").append(attr).append("\n");
            }
        }

        if (this.method != null) {
            for (String meth : this.method) {
                puml.append("  {method} ").append(meth).append("\n");
            }
        }

        puml.append("}\n");

        return puml.toString();
    }

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
