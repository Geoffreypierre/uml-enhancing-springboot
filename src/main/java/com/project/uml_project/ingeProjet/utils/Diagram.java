package com.project.uml_project.ingeProjet.utils;

import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//Représente l'uml diagramme
public class Diagram {

    private net.sourceforge.plantuml.core.Diagram diagram;

    public Diagram(net.sourceforge.plantuml.core.Diagram diagram) {
        this.diagram = diagram;
    }

    public Node toKnowledgeGraph() {
        if (!(diagram instanceof ClassDiagram)) {
            return new Node(new ArrayList<>());
        }

        ClassDiagram classDiagram = (ClassDiagram) diagram;
        Collection<Node> rootChildren = new ArrayList<>();

        // Create a map to store entity nodes for relationship processing
        Map<Entity, Node> entityNodeMap = new HashMap<>();

        // Get all entities from the diagram
        Collection<Entity> allEntities = new ArrayList<>();

        // Try to get all entities using various approaches

        // Approach 1: Try using entitySupport() which should have all entities
        try {
            var entitySupport = classDiagram.getClass().getMethod("entitySupport").invoke(classDiagram);
            if (entitySupport != null) {
                try {
                    // Try getting all entities from entitySupport
                    @SuppressWarnings("unchecked")
                    Collection<Entity> supportedEntities = (Collection<Entity>) entitySupport.getClass()
                            .getMethod("getAllEntities").invoke(entitySupport);
                    if (supportedEntities != null && !supportedEntities.isEmpty()) {
                        allEntities.addAll(supportedEntities);
                        System.out.println("Successfully extracted " + supportedEntities.size() +
                                         " entities from entitySupport().getAllEntities()");
                    }
                } catch (Exception ex1) {
                    // Try other methods on entitySupport
                    try {
                        @SuppressWarnings("unchecked")
                        Collection<Entity> supportedEntities = (Collection<Entity>) entitySupport.getClass()
                                .getMethod("values").invoke(entitySupport);
                        if (supportedEntities != null && !supportedEntities.isEmpty()) {
                            allEntities.addAll(supportedEntities);
                            System.out.println("Successfully extracted " + supportedEntities.size() +
                                             " entities from entitySupport().values()");
                        }
                    } catch (Exception ex2) {
                        System.err.println("Could not extract entities from entitySupport: " + ex2.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: entitySupport() not available: " + e.getMessage());
        }

        // Approach 2: Try getEntityFactory().leafs() if entities not found yet
        if (allEntities.isEmpty()) {
            try {
                var entityFactory = classDiagram.getClass().getMethod("getEntityFactory").invoke(classDiagram);
                if (entityFactory != null) {
                    try {
                        @SuppressWarnings("unchecked")
                        Collection<Entity> leafs = (Collection<Entity>) entityFactory.getClass()
                                .getMethod("leafs").invoke(entityFactory);
                        if (leafs != null && !leafs.isEmpty()) {
                            allEntities.addAll(leafs);
                            System.out.println("Successfully extracted " + leafs.size() +
                                             " entities from getEntityFactory().leafs()");
                        }
                    } catch (Exception ex) {
                        System.err.println("Could not extract from leafs(): " + ex.getMessage());
                    }

                    // If still empty, try getAllObjects
                    if (allEntities.isEmpty()) {
                        try {
                            @SuppressWarnings("unchecked")
                            Collection<Entity> allObjects = (Collection<Entity>) entityFactory.getClass()
                                    .getMethod("getAllObjects").invoke(entityFactory);
                            if (allObjects != null && !allObjects.isEmpty()) {
                                allEntities.addAll(allObjects);
                                System.out.println("Successfully extracted " + allObjects.size() +
                                                 " entities from getEntityFactory().getAllObjects()");
                            }
                        } catch (Exception ex) {
                            System.err.println("Could not extract from getAllObjects(): " + ex.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not access getEntityFactory(): " + e.getMessage());
            }
        }

        // Approach 3: Collect entities from links (for diagrams with relationships)
        Collection<Link> links = classDiagram.getLinks();
        if (links != null && !links.isEmpty()) {
            Collection<Entity> entitiesFromLinks = collectUniqueEntities(links);
            for (Entity entity : entitiesFromLinks) {
                if (!allEntities.contains(entity)) {
                    allEntities.add(entity);
                }
            }
            System.out.println("Extracted " + entitiesFromLinks.size() + " entities from links");
        }

        // Process all entities (classes, interfaces, enums, etc.)
        if (!allEntities.isEmpty()) {
            System.out.println("Processing " + allEntities.size() + " entities total");
            for (Entity entity : allEntities) {
                Node entityNode = createEntityNode(entity);
                entityNodeMap.put(entity, entityNode);
                rootChildren.add(entityNode);
                System.out.println("  - Added entity: " + entity.getName());
            }
        } else {
            System.err.println("ERROR: No entities found in the diagram after trying all approaches. " +
                             "The diagram may be empty or malformed.");
        }

        // Process relationships (links between entities)
        if (links != null && !links.isEmpty()) {
            System.out.println("Processing " + links.size() + " relationships");
            for (Link link : links) {
                Node relationshipNode = createRelationshipNode(link, entityNodeMap);
                rootChildren.add(relationshipNode);
            }
        }

        // Return the root node containing all entities and relationships
        return new Node(rootChildren);
    }

    private Collection<Entity> collectUniqueEntities(Collection<Link> links) {
        Collection<Entity> entities = new ArrayList<>();
        for (Link link : links) {
            if (!entities.contains(link.getEntity1())) {
                entities.add(link.getEntity1());
            }
            if (!entities.contains(link.getEntity2())) {
                entities.add(link.getEntity2());
            }
        }
        return entities;
    }

    private Node createEntityNode(Entity entity) {
        Node entityNode = new Node(new ArrayList<>());

        // Set entity name
        if (entity.getName() != null) {
            entityNode.setName(entity.getName());
        }

        // Extract attributes and methods
        Collection<String> attributes = new ArrayList<>();
        Collection<String> methods = new ArrayList<>();

        // Extract fields (attributes)
        if (entity.getBodier() != null && entity.getBodier().getFieldsToDisplay() != null) {
            for (Object field : entity.getBodier().getFieldsToDisplay()) {
                if (field != null) {
                    String fieldStr = field.toString();
                    // Clean up the field string (remove visibility markers, etc.)
                    attributes.add(cleanMemberString(fieldStr));
                }
            }
        }

        // Extract methods
        if (entity.getBodier() != null && entity.getBodier().getMethodsToDisplay() != null) {
            for (Object method : entity.getBodier().getMethodsToDisplay()) {
                if (method != null) {
                    String methodStr = method.toString();
                    // Clean up the method string
                    methods.add(cleanMemberString(methodStr));
                }
            }
        }

        // Set attributes and methods on the node
        if (!attributes.isEmpty()) {
            entityNode.setAttribute(attributes);
        }
        if (!methods.isEmpty()) {
            entityNode.setMethod(methods);
        }

        return entityNode;
    }

    private String cleanMemberString(String member) {
        // Remove visibility markers like {field}, {method}, +, -, #, ~
        String cleaned = member.replaceAll("\\{[^}]*\\}", "").trim();
        cleaned = cleaned.replaceAll("^[+\\-#~]\\s*", "");
        return cleaned;
    }

    private Node createRelationshipNode(Link link, Map<Entity, Node> entityNodeMap) {
        Entity entity1 = link.getEntity1();
        Entity entity2 = link.getEntity2();

        Node relationshipNode = new Node(new ArrayList<>());

        // Set relationship name based on connected entities
        String entity1Name = entity1.getName() != null ? entity1.getName() : "Unknown";
        String entity2Name = entity2.getName() != null ? entity2.getName() : "Unknown";
        String linkLabel = link.getLabel() != null ? link.getLabel().toString() : "";

        // Create a descriptive relationship name
        StringBuilder relationshipName = new StringBuilder();
        relationshipName.append(entity1Name);
        relationshipName.append("_to_");
        relationshipName.append(entity2Name);
        if (!linkLabel.isEmpty()) {
            relationshipName.append("_").append(linkLabel.replaceAll("\\s+", "_"));
        }

        relationshipNode.setName(relationshipName.toString());

        // Add relationship metadata as attributes
        Collection<String> relationshipAttrs = new ArrayList<>();
        relationshipAttrs.add("type: " + link.getType().toString());
        if (!linkLabel.isEmpty()) {
            relationshipAttrs.add("label: " + linkLabel);
        }
        if (link.getQuantifier1() != null && !link.getQuantifier1().isEmpty()) {
            relationshipAttrs.add("cardinality1: " + link.getQuantifier1());
        }
        if (link.getQuantifier2() != null && !link.getQuantifier2().isEmpty()) {
            relationshipAttrs.add("cardinality2: " + link.getQuantifier2());
        }
        relationshipNode.setAttribute(relationshipAttrs);

        return relationshipNode;
    }

    public void toPuml() {
        // TODO: Implement toPuml method
    }

}
