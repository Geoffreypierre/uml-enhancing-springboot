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

        // Try to get entities using getEntityFactory().leafs()
        try {
            var entityFactory = classDiagram.getClass().getMethod("getEntityFactory").invoke(classDiagram);
            if (entityFactory != null) {
                @SuppressWarnings("unchecked")
                Collection<Entity> leafs = (Collection<Entity>) entityFactory.getClass().getMethod("leafs")
                        .invoke(entityFactory);
                allEntities.addAll(leafs);
            }
        } catch (Exception e) {
            // If reflection fails, fall back to collecting from links only
            System.err.println("Warning: Could not extract entities directly, using links only: " + e.getMessage());
        }

        // Also collect entities from links
        Collection<Link> links = classDiagram.getLinks();
        Collection<Entity> entitiesFromLinks = collectUniqueEntities(links);

        // Merge both collections (avoid duplicates)
        for (Entity entity : entitiesFromLinks) {
            if (!allEntities.contains(entity)) {
                allEntities.add(entity);
            }
        }

        // Process all entities (classes, interfaces, enums, etc.)
        for (Entity entity : allEntities) {
            Node entityNode = createEntityNode(entity);
            entityNodeMap.put(entity, entityNode);
            rootChildren.add(entityNode);
        }

        // Process relationships (links between entities)
        for (Link link : links) {
            Node relationshipNode = createRelationshipNode(link, entityNodeMap);
            rootChildren.add(relationshipNode);
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
