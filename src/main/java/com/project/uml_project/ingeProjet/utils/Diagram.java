
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

        // Process relationships (links between entities) to extract entities
        Collection<Link> links = classDiagram.getLinks();

        // Collect all unique entities from the links
        Collection<Entity> entities = collectUniqueEntities(links);

        // Process all entities (classes, interfaces, enums, etc.)
        for (Entity entity : entities) {
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
        Collection<Node> entityChildren = new ArrayList<>();

        // Create nodes for the entity's body content (attributes and methods)
        if (entity.getBodier() != null) {
            addFieldNodes(entity, entityChildren);
            addMethodNodes(entity, entityChildren);
        }

        return new Node(entityChildren);
    }

    private void addFieldNodes(Entity entity, Collection<Node> entityChildren) {
        if (entity.getBodier().getFieldsToDisplay() != null) {
            for (Object field : entity.getBodier().getFieldsToDisplay()) {
                // Field information can be extracted and stored in the Node if needed
                Node fieldNode = new Node(new ArrayList<>());
                entityChildren.add(fieldNode);
            }
        }
    }

    private void addMethodNodes(Entity entity, Collection<Node> entityChildren) {
        if (entity.getBodier().getMethodsToDisplay() != null) {
            for (Object method : entity.getBodier().getMethodsToDisplay()) {
                // Method information can be extracted and stored in the Node if needed
                Node methodNode = new Node(new ArrayList<>());
                entityChildren.add(methodNode);
            }
        }
    }

    private Node createRelationshipNode(Link link, Map<Entity, Node> entityNodeMap) {
        Entity entity1 = link.getEntity1();
        Entity entity2 = link.getEntity2();

        Collection<Node> relationshipChildren = new ArrayList<>();

        // Add reference to connected entities if they exist in our map
        if (entityNodeMap.containsKey(entity1)) {
            relationshipChildren.add(entityNodeMap.get(entity1));
        }
        if (entityNodeMap.containsKey(entity2)) {
            relationshipChildren.add(entityNodeMap.get(entity2));
        }

        return new Node(relationshipChildren);
    }

    public void toPuml() {
        // TODO: Implement toPuml method
    }

}
