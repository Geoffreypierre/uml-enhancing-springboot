package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.LLM.LLMProvider;
import com.project.uml_project.ingeProjet.utils.Diagram;

public class EnhancedPumlBuilder {

    private java.util.Collection<Concept> concepts;
    private Diagram originalDiagram;
    private float filterTreeshold;
    private LLMProvider llmProvider;
    private String exportedResult;

    public EnhancedPumlBuilder() {
    }

    // Doit améliorer l'uml original
    public void enhance() throws Exception {
        // Guard: check if concepts is null or empty
        if (concepts == null || concepts.isEmpty()) {
            System.out.println("EnhancedPumlBuilder: No concepts to enhance");
            return;
        }

        System.out.println("EnhancedPumlBuilder: Starting enhancement with " + concepts.size() + " concepts");

        // Set the LLM provider on each concept first
        for (Concept concept : concepts) {
            concept.setLlmProvider(llmProvider);
        }
        
        // Appelle le LLM pour noter les concepts et les nommer
        for (Concept concept : concepts) {
            try {
                float score = concept.relevanceScore();
                String newName = concept.setNameFromLLM();
                System.out.println("  Concept '" + concept.getOriginalName() +
                                 "' -> '" + newName + "' (score: " + score + ")");
            } catch (Exception e) {
                System.err.println("  Warning: Could not score concept '" + concept.getOriginalName() + "': " + e.getMessage());
            }
        }

        // Filtre les concepts avec le treshold
        int beforeFilter = concepts.size();
        concepts.removeIf(concept -> {
            try {
                float score = concept.relevanceScore();
                String name = concept.getOriginalName();

                // For relationship concepts (containing "_to_"), apply a stricter threshold
                // since they are often less significant than entity concepts
                if (name != null && name.contains("_to_")) {
                    // Relationships need higher relevance score (e.g., 1.5x the threshold)
                    float relationshipThreshold = Math.min(1.0f, filterTreeshold * 1.5f);
                    return score < relationshipThreshold;
                }

                // For entity concepts, use the normal threshold
                return score < filterTreeshold;
            } catch (Exception e) {
                // If we can't get the relevance score, filter out the concept
                System.err.println("  Warning: Filtering out concept due to scoring error");
                return true;
            }
        });
        
        System.out.println("EnhancedPumlBuilder: Filtered from " + beforeFilter + 
                         " to " + concepts.size() + " concepts (threshold: " + filterTreeshold + ")");
    }

    // exporte le nouveau diagramme en puml
    public String export() {
        StringBuilder result = new StringBuilder();
        result.append("@startuml\n");

        if (concepts != null && !concepts.isEmpty()) {
            // Analyze concepts to find common attributes/methods and create abstractions
            var enhancement = analyzeAndCreateAbstractions();

            // Export abstract parent classes
            if (!enhancement.abstractClasses.isEmpty()) {
                for (var abstractClass : enhancement.abstractClasses) {
                    result.append(abstractClass);
                    result.append("\n");
                }
            }

            // Export refactored concrete classes with inheritance
            if (!enhancement.concreteClasses.isEmpty()) {
                for (var concreteClass : enhancement.concreteClasses) {
                    result.append(concreteClass);
                    result.append("\n");
                }
            }

            // Export inheritance relationships
            if (!enhancement.inheritanceRelations.isEmpty()) {
                for (var relation : enhancement.inheritanceRelations) {
                    result.append(relation);
                    result.append("\n");
                }
            }

            // Export other relationships (associations)
            if (!enhancement.otherRelations.isEmpty()) {
                result.append("\n");
                for (var relation : enhancement.otherRelations) {
                    result.append(relation);
                    result.append("\n");
                }
            }
        }

        result.append("@enduml\n");
        return result.toString();
    };

    private static class Enhancement {
        java.util.List<String> abstractClasses = new java.util.ArrayList<>();
        java.util.List<String> concreteClasses = new java.util.ArrayList<>();
        java.util.List<String> inheritanceRelations = new java.util.ArrayList<>();
        java.util.List<String> otherRelations = new java.util.ArrayList<>();
    }

    private Enhancement analyzeAndCreateAbstractions() {
        Enhancement enhancement = new Enhancement();

        // Separate entity concepts from relationship concepts
        java.util.List<Concept> entityConcepts = new java.util.ArrayList<>();
        java.util.List<Concept> relationshipConcepts = new java.util.ArrayList<>();

        for (Concept concept : concepts) {
            String name = concept.getOriginalName();
            if (name != null && name.contains("_to_")) {
                relationshipConcepts.add(concept);
            } else if (name != null && !name.matches("[0-9a-f-]+")) {
                // Not a UUID, it's a real entity
                entityConcepts.add(concept);
            }
        }

        // Find common attributes and methods across entities
        java.util.Map<String, Integer> attributeFrequency = new java.util.HashMap<>();
        java.util.Map<String, Integer> methodFrequency = new java.util.HashMap<>();

        for (Concept entity : entityConcepts) {
            for (String attr : entity.getAttribute()) {
                attributeFrequency.put(attr, attributeFrequency.getOrDefault(attr, 0) + 1);
            }
            for (String method : entity.getMethod()) {
                methodFrequency.put(method, methodFrequency.getOrDefault(method, 0) + 1);
            }
        }

        // Identify common members (appear in 2+ entities)
        java.util.Set<String> commonAttributes = new java.util.HashSet<>();
        java.util.Set<String> commonMethods = new java.util.HashSet<>();

        for (var entry : attributeFrequency.entrySet()) {
            if (entry.getValue() >= 2) {
                commonAttributes.add(entry.getKey());
            }
        }

        for (var entry : methodFrequency.entrySet()) {
            if (entry.getValue() >= 2) {
                commonMethods.add(entry.getKey());
            }
        }

        // Track child class names for filtering sibling associations
        java.util.Set<String> childClassNames = new java.util.HashSet<>();

        // Create abstract parent class if there are common members
        if (!commonAttributes.isEmpty() || !commonMethods.isEmpty()) {
            // Generate a meaningful name for the abstract class using LLM
            String abstractClassName = generateAbstractClassName(commonAttributes, commonMethods);

            StringBuilder abstractClass = new StringBuilder();
            abstractClass.append("abstract class ").append(abstractClassName).append(" {\n");

            for (String attr : commonAttributes) {
                abstractClass.append("  ").append(attr).append("\n");
            }

            for (String method : commonMethods) {
                abstractClass.append("  +").append(method).append("\n");
            }

            abstractClass.append("}");
            enhancement.abstractClasses.add(abstractClass.toString());

            // Create refactored concrete classes
            for (Concept entity : entityConcepts) {
                StringBuilder concreteClass = new StringBuilder();
                concreteClass.append("class ").append(entity.getOriginalName()).append(" {\n");

                // Add only unique attributes
                for (String attr : entity.getAttribute()) {
                    if (!commonAttributes.contains(attr)) {
                        concreteClass.append("  ").append(attr).append("\n");
                    }
                }

                // Add only unique methods
                for (String method : entity.getMethod()) {
                    if (!commonMethods.contains(method)) {
                        concreteClass.append("  +").append(method).append("\n");
                    }
                }

                concreteClass.append("}");
                enhancement.concreteClasses.add(concreteClass.toString());

                // Add inheritance relationship
                enhancement.inheritanceRelations.add(
                        abstractClassName + " <|-- " + entity.getOriginalName());
            }
        } else {
            // No common attributes, just export as is
            for (Concept entity : entityConcepts) {
                StringBuilder concreteClass = new StringBuilder();
                concreteClass.append("class ").append(entity.getOriginalName()).append(" {\n");

                for (String attr : entity.getAttribute()) {
                    concreteClass.append("  ").append(attr).append("\n");
                }

                for (String method : entity.getMethod()) {
                    concreteClass.append("  +").append(method).append("\n");
                }

                concreteClass.append("}");
                enhancement.concreteClasses.add(concreteClass.toString());
            }
        }

        // Export relationship concepts as associations
        for (Concept relationship : relationshipConcepts) {
            String name = relationship.getOriginalName();
            if (name != null && name.contains("_to_")) {
                // Parse relationship name: Entity1_to_Entity2_[label]
                String[] parts = name.split("_to_");
                if (parts.length >= 2) {
                    String entity1 = parts[0].replaceAll("_NULL$", ""); // Remove _NULL suffix
                    String rest = parts[1];

                    // Extract entity2 and label
                    String entity2;
                    String label = "";
                    int bracketIndex = rest.indexOf("_[");
                    if (bracketIndex > 0) {
                        entity2 = rest.substring(0, bracketIndex).replaceAll("_NULL$", ""); // Remove _NULL suffix
                        label = rest.substring(bracketIndex + 2, rest.length() - 1);
                    } else {
                        entity2 = rest.replaceAll("_NULL$", ""); // Remove _NULL suffix
                    }

                    // Check if both entities exist in the entity concepts
                    boolean entity1Exists = entityConcepts.stream()
                            .anyMatch(e -> e.getOriginalName().equals(entity1));
                    boolean entity2Exists = entityConcepts.stream()
                            .anyMatch(e -> e.getOriginalName().equals(entity2));

                    // CRITICAL: Skip relationships between sibling classes (both are children of the same abstract class)
                    boolean bothAreSiblings = childClassNames.contains(entity1) &&
                                              childClassNames.contains(entity2);

                    // Only create association if both entities exist AND they are not siblings
                    if (entity1Exists && entity2Exists && !bothAreSiblings) {
                        // Extract cardinalities from attributes
                        String card1 = "1"; // Default cardinality (changed from "*")
                        String card2 = "1"; // Default cardinality (changed from "*")
                        for (String attr : relationship.getAttribute()) {
                            if (attr.startsWith("cardinality1:")) {
                                String extractedCard = attr.substring(13).trim();
                                if (!extractedCard.isEmpty() && !extractedCard.equals("null")) {
                                    card1 = extractedCard;
                                }
                            } else if (attr.startsWith("cardinality2:")) {
                                String extractedCard = attr.substring(13).trim();
                                if (!extractedCard.isEmpty() && !extractedCard.equals("null")) {
                                    card2 = extractedCard;
                                }
                            }
                        }

                        // Generate a default label if empty
                        if (label == null || label.trim().isEmpty()) {
                            label = "associated";
                        }

                        // Create association with proper formatting
                        String association = entity1 + " \"" + card1 + "\" -- \"" + card2 + "\" " +
                                entity2 + " : " + label;
                        enhancement.otherRelations.add(association);
                    }
                }
            }
        }

        return enhancement;
    }

    /**
     * Generate a meaningful name for the abstract parent class using LLM
     */
    private String generateAbstractClassName(java.util.Set<String> commonAttributes,
            java.util.Set<String> commonMethods) {
        // If no LLM provider, use default name
        if (llmProvider == null) {
            return "Entity";
        }

        try {
            // Build a prompt that emphasizes generic naming, not specific domains
            String prompt = "You are naming an abstract parent class that will be inherited by multiple concrete classes.\n\n" +
                    "Common Attributes (inherited by all children): " + commonAttributes.toString() + "\n" +
                    "Common Methods (inherited by all children): " + commonMethods.toString() + "\n\n" +
                    "Generate a generic, domain-neutral abstract class name that:\n" +
                    "- Represents the shared characteristics across multiple entity types\n" +
                    "- Is NOT domain-specific (avoid names like HumanEntity, PersonBase, etc.)\n" +
                    "- Is simple and general (e.g., Entity, Living Entity, Organism, LivingBeing, etc.)\n" +
                    "- Uses PascalCase\n" +
                    "- Is one or two words maximum\n\n" +
                    "Respond with ONLY the class name, nothing else. No quotes, no explanations.";

            String suggestedName = llmProvider.request(prompt);

            // Clean up the response
            suggestedName = suggestedName.trim()
                    .replaceAll("^\"|\"$", "") // Remove quotes
                    .replaceAll("^'|'$", "")
                    .replaceAll("\\s+", " ") // Normalize spaces
                    .split("\\s+")[0]; // Take first word

            // Validate it's a reasonable class name (not empty, starts with uppercase)
            if (suggestedName.isEmpty() || !Character.isUpperCase(suggestedName.charAt(0))) {
                return "Entity";
            }

            return suggestedName;
        } catch (Exception e) {
            // If LLM call fails, use default name
            return "Entity";
        }
    }

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
