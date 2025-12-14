package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.LLM.LLMProvider;
import com.project.uml_project.ingeProjet.utils.Diagram;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

        int originalSize = concepts.size();
        System.out.println("EnhancedPumlBuilder: Starting enhancement with " + originalSize + " concepts");

        // Pre-filter obvious low-quality concepts before expensive LLM calls
        java.util.List<Concept> filteredConcepts = concepts.stream()
                .filter(concept -> {
                    String name = concept.getOriginalName();
                    if (name == null)
                        return false;

                    // Filter out auto-generated relationship concepts
                    if (name.contains("_to_") && name.contains("_NULL")) {
                        System.out.println("  Pre-filtered: " + name + " (auto-generated relationship)");
                        return false;
                    }

                    // Filter out UUID-like names
                    if (name.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                        System.out.println("  Pre-filtered: " + name + " (UUID)");
                        return false;
                    }

                    // Filter out concepts with no attributes and no methods
                    if ((concept.getAttribute() == null || concept.getAttribute().isEmpty()) &&
                            (concept.getMethod() == null || concept.getMethod().isEmpty())) {
                        System.out.println("  Pre-filtered: " + name + " (no attributes/methods)");
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        System.out.println("Pre-filtered from " + originalSize + " to " + filteredConcepts.size() + " concepts");

        if (filteredConcepts.isEmpty()) {
            concepts = new java.util.ArrayList<>();
            return;
        }

        // Set the LLM provider on each concept
        for (Concept concept : filteredConcepts) {
            concept.setLlmProvider(llmProvider);
        }

        // Process LLM calls in parallel using thread pool
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        java.util.List<CompletableFuture<Void>> futures = new java.util.ArrayList<>();

        for (Concept concept : filteredConcepts) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    float score = concept.relevanceScore();
                    String newName = concept.setNameFromLLM();
                    System.out.println("  Concept '" + concept.getOriginalName() +
                            "' -> '" + newName + "' (score: " + score + ")");
                } catch (Exception e) {
                    System.err.println("  Warning: Could not process concept '" + concept.getOriginalName() + "': "
                            + e.getMessage());
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all LLM calls to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // Filter by relevance score threshold
        int beforeThreshold = filteredConcepts.size();
        java.util.List<Concept> finalConcepts = filteredConcepts.stream()
                .filter(concept -> {
                    try {
                        float score = concept.relevanceScore();
                        return score >= filterTreeshold;
                    } catch (Exception e) {
                        System.err.println("  Warning: Filtering out concept due to scoring error");
                        return false;
                    }
                })
                .collect(Collectors.toList());

        concepts = finalConcepts;
        System.out.println("EnhancedPumlBuilder: Filtered by threshold from " + beforeThreshold +
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

        // Cluster entities into natural groups before creating abstractions
        java.util.List<java.util.List<Concept>> clusters = clusterEntitiesBySimarity(entityConcepts);

        // Process each cluster separately to create hierarchical abstractions
        if (clusters.size() > 1) {
            System.out.println("Found " + clusters.size() + " natural groupings of classes");
            for (int i = 0; i < clusters.size(); i++) {
                java.util.List<Concept> cluster = clusters.get(i);
                if (cluster.size() >= 2) {
                    System.out.println("  Cluster " + (i + 1) + ": " + cluster.size() + " classes");
                    createAbstractionForCluster(cluster, enhancement);
                } else {
                    // Single entity, export without abstraction
                    exportConcreteClass(cluster.get(0), enhancement, null, java.util.Collections.emptySet(),
                            java.util.Collections.emptySet());
                }
            }
            return enhancement;
        }

        // Fall back to original logic if clustering produces only one group
        entityConcepts = clusters.isEmpty() ? entityConcepts : clusters.get(0);

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

                    // CRITICAL: Skip relationships between sibling classes (both are children of
                    // the same abstract class)
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
     * This is deprecated - use generateAbstractClassNameForCluster for better
     * context-aware naming
     */
    @Deprecated
    private String generateAbstractClassName(java.util.Set<String> commonAttributes,
            java.util.Set<String> commonMethods) {
        return "Entity"; // Fallback for legacy code paths
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

    /**
     * Cluster entities by similarity based on shared methods and attributes.
     * Uses a simple similarity threshold to group related classes.
     */
    private java.util.List<java.util.List<Concept>> clusterEntitiesBySimarity(java.util.List<Concept> entities) {
        if (entities.size() <= 2) {
            // Too few entities to cluster meaningfully
            return java.util.Collections.singletonList(entities);
        }

        java.util.List<java.util.List<Concept>> clusters = new java.util.ArrayList<>();
        java.util.Set<Concept> processed = new java.util.HashSet<>();

        for (Concept entity : entities) {
            if (processed.contains(entity))
                continue;

            java.util.List<Concept> cluster = new java.util.ArrayList<>();
            cluster.add(entity);
            processed.add(entity);

            // Find similar entities
            for (Concept other : entities) {
                if (processed.contains(other))
                    continue;

                double similarity = calculateSimilarity(entity, other);
                // Threshold of 0.3 means at least 30% of methods/attributes in common
                if (similarity >= 0.3) {
                    cluster.add(other);
                    processed.add(other);
                }
            }

            clusters.add(cluster);
        }

        return clusters;
    }

    /**
     * Calculate similarity between two concepts based on Jaccard index of methods
     * and attributes.
     */
    private double calculateSimilarity(Concept c1, Concept c2) {
        java.util.Set<String> methods1 = new java.util.HashSet<>(c1.getMethod());
        java.util.Set<String> methods2 = new java.util.HashSet<>(c2.getMethod());
        java.util.Set<String> attrs1 = new java.util.HashSet<>(c1.getAttribute());
        java.util.Set<String> attrs2 = new java.util.HashSet<>(c2.getAttribute());

        // Combine methods and attributes
        java.util.Set<String> set1 = new java.util.HashSet<>();
        set1.addAll(methods1);
        set1.addAll(attrs1);

        java.util.Set<String> set2 = new java.util.HashSet<>();
        set2.addAll(methods2);
        set2.addAll(attrs2);

        if (set1.isEmpty() && set2.isEmpty())
            return 0.0;

        // Calculate Jaccard similarity
        java.util.Set<String> intersection = new java.util.HashSet<>(set1);
        intersection.retainAll(set2);

        java.util.Set<String> union = new java.util.HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * Create abstraction for a cluster of similar entities.
     */
    private void createAbstractionForCluster(java.util.List<Concept> cluster, Enhancement enhancement) {
        // Find common attributes and methods
        java.util.Map<String, Integer> attributeFrequency = new java.util.HashMap<>();
        java.util.Map<String, Integer> methodFrequency = new java.util.HashMap<>();

        for (Concept entity : cluster) {
            for (String attr : entity.getAttribute()) {
                attributeFrequency.put(attr, attributeFrequency.getOrDefault(attr, 0) + 1);
            }
            for (String method : entity.getMethod()) {
                methodFrequency.put(method, methodFrequency.getOrDefault(method, 0) + 1);
            }
        }

        // Get total unique methods and attributes across cluster
        int totalUniqueMembers = attributeFrequency.size() + methodFrequency.size();

        // Identify common members (appear in at least 50% of entities in cluster)
        int minFrequency = Math.max(2, cluster.size() / 2);
        java.util.Set<String> commonAttributes = new java.util.HashSet<>();
        java.util.Set<String> commonMethods = new java.util.HashSet<>();

        for (var entry : attributeFrequency.entrySet()) {
            if (entry.getValue() >= minFrequency) {
                commonAttributes.add(entry.getKey());
            }
        }

        for (var entry : methodFrequency.entrySet()) {
            if (entry.getValue() >= minFrequency) {
                commonMethods.add(entry.getKey());
            }
        }

        // God class detection: if common members > 50% of all unique members, this is a
        // god class
        int commonMemberCount = commonAttributes.size() + commonMethods.size();
        double commonRatio = totalUniqueMembers == 0 ? 0.0 : (double) commonMemberCount / totalUniqueMembers;

        if (commonRatio > 0.5 && cluster.size() > 3) {
            System.out.println("  Warning: Detected potential god class (" + commonRatio * 100
                    + "% common members). Skipping abstraction.");
            // Export classes without abstraction to avoid god class
            for (Concept entity : cluster) {
                exportConcreteClass(entity, enhancement, null, java.util.Collections.emptySet(),
                        java.util.Collections.emptySet());
            }
            return;
        }

        // Create abstract parent if there are meaningful common members
        if (!commonAttributes.isEmpty() || !commonMethods.isEmpty()) {
            // Generate domain-specific name for this cluster
            String abstractClassName = generateAbstractClassNameForCluster(cluster, commonAttributes, commonMethods);

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

            // Create concrete classes with inheritance
            for (Concept entity : cluster) {
                exportConcreteClass(entity, enhancement, abstractClassName, commonAttributes, commonMethods);
            }
        } else {
            // No common members, export without abstraction
            for (Concept entity : cluster) {
                exportConcreteClass(entity, enhancement, null, java.util.Collections.emptySet(),
                        java.util.Collections.emptySet());
            }
        }
    }

    /**
     * Export a concrete class, optionally with inheritance.
     */
    private void exportConcreteClass(Concept entity, Enhancement enhancement,
            String parentClass,
            java.util.Set<String> commonAttributes,
            java.util.Set<String> commonMethods) {
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

        // Add inheritance if parent exists
        if (parentClass != null) {
            enhancement.inheritanceRelations.add(parentClass + " <|-- " + entity.getOriginalName());
        }
    }

    /**
     * Generate abstraction name with domain context from cluster.
     */
    private String generateAbstractClassNameForCluster(java.util.List<Concept> cluster,
            java.util.Set<String> commonAttributes,
            java.util.Set<String> commonMethods) {
        if (llmProvider == null) {
            return "Entity";
        }

        try {
            // Build class names list for context
            String classNames = cluster.stream()
                    .map(Concept::getOriginalName)
                    .collect(Collectors.joining(", "));

            String prompt = "You are naming an abstract parent class for a group of related concrete classes.\n\n" +
                    "Concrete Classes: " + classNames + "\n" +
                    "Common Attributes: " + commonAttributes.toString() + "\n" +
                    "Common Methods: " + commonMethods.toString() + "\n\n" +
                    "Analyze the class names and shared characteristics to identify their domain.\n" +
                    "Generate a meaningful, domain-specific abstract class name that:\n" +
                    "- Captures the essence of what these classes represent\n" +
                    "- Is specific to their domain (e.g., Connection, Logger, Parser, Shape, Payment, Worker, Request)\n"
                    +
                    "- Is NOT overly generic (avoid Entity, BaseEntity, etc.)\n" +
                    "- Uses PascalCase\n" +
                    "- Is 1-2 words maximum\n\n" +
                    "Examples:\n" +
                    "- Classes: MySQLRepository, PostgreSQLRepository, MongoDBRepository -> 'Repository'\n" +
                    "- Classes: FileLogger, ConsoleLogger, DatabaseLogger -> 'Logger'\n" +
                    "- Classes: JSONParser, XMLParser, YAMLParser -> 'Parser'\n" +
                    "- Classes: Rectangle, Circle, Triangle -> 'Shape'\n\n" +
                    "Respond with ONLY the class name, nothing else.";

            String suggestedName = llmProvider.request(prompt);
            suggestedName = suggestedName.trim()
                    .replaceAll("^\"|\"$", "")
                    .replaceAll("^'|'$", "")
                    .split("\\s+")[0];

            if (suggestedName.isEmpty() || !Character.isUpperCase(suggestedName.charAt(0))) {
                return "Entity";
            }

            return suggestedName;
        } catch (Exception e) {
            System.err.println("Warning: LLM name generation failed: " + e.getMessage());
            return "Entity";
        }
    }

};
