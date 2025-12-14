package com.project.uml_project.ingeProjet.fca4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import com.project.uml_project.ingeProjet.main.Concept;
import com.project.uml_project.ingeProjet.utils.Node;

import fr.lirmm.fca4j.core.BinaryContext;
import fr.lirmm.fca4j.iset.ISetFactory;
import fr.lirmm.fca4j.iset.std.BitSetFactory;

/**
 * FCA4JAdapter utilisant l'API fca4j-core (0.4.4).
 * - generate prend une Node racine (KG)
 * - construit un BinaryContext formel à partir des nodes du graphe
 * - construit le treillis de concepts via l'algorithme LinCbO
 * - convertit les concepts formels en Concept (projet)
 */
public class FCA4JAdapter {

    public FCA4JAdapter() {
        // constructeur vide — pas de CLI ni réflexion
    }

    /**
     * Prend une Node racine représentant le KG et retourne la collection de Concept
     * construite à partir des concepts extraits par FCA4J.
     */
    public Collection<Concept> generate(Node root) {
        if (root == null)
            return Collections.emptyList();

        // 1) Parcours du graphe (BFS) pour collecter toutes les nodes
        List<Node> nodes = collectGraphNodes(root);

        // 2) Construire la liste d'objets (noms) et l'ensemble d'attributs uniques
        List<String> objectNames = new ArrayList<>();
        LinkedHashSet<String> allAttributes = new LinkedHashSet<>();

        // Map objectName -> set(attributes)
        Map<String, Set<String>> incidence = new LinkedHashMap<>();

        for (Node n : nodes) {
            // extraction simple via getters fournis dans ta classe Node
            String name = safeGetName(n);
            if (name == null || name.isEmpty()) {
                name = UUID.randomUUID().toString();
            }
            objectNames.add(name);

            // attributes from getAttribute()
            Collection<String> attrs = n.getAttribute();
            Set<String> attrSet = new LinkedHashSet<>();
            if (attrs != null) {
                for (String a : attrs) {
                    if (a != null && !a.trim().isEmpty()) {
                        String clean = a.trim();
                        attrSet.add(clean);
                        allAttributes.add(clean);
                    }
                }
            }

            // methods as attributes prefixed to avoid collision
            Collection<String> methods = n.getMethod();
            if (methods != null) {
                for (String m : methods) {
                    if (m != null && !m.trim().isEmpty()) {
                        String pref = "m:" + m.trim();
                        attrSet.add(pref);
                        allAttributes.add(pref);
                    }
                }
            }

            // optionally include children names as attributes
            Collection<Node> children = n.getChildren();
            if (children != null) {
                for (Node c : children) {
                    String cn = safeGetName(c);
                    if (cn != null && !cn.trim().isEmpty()) {
                        String childAttr = "child:" + cn.trim();
                        attrSet.add(childAttr);
                        allAttributes.add(childAttr);
                    }
                }
            }

            incidence.put(name, attrSet);
        }

        // 3) Construire le BinaryContext formel via fca4j API
        List<String> attributesList = new ArrayList<>(allAttributes);

        // Si pas d'objets, retourner une liste vide
        if (objectNames.isEmpty() || attributesList.isEmpty()) {
            return Collections.emptyList();
        }

        // Créer une factory pour les ISets (BitSet est efficace pour les contextes
        // binaires)
        ISetFactory factory = new BitSetFactory();

        // Créer le contexte avec le nombre d'objets et d'attributs
        int nbObjects = objectNames.size();
        int nbAttributes = attributesList.size();

        BinaryContext context = new BinaryContext(nbObjects, nbAttributes, "KnowledgeGraph", factory);

        // Ajouter les noms des objets et des attributs via
        // addObjectName/addAttributeName
        // qui ajoutent aux ArrayLists internes
        for (String objName : objectNames) {
            context.addObjectName(objName);
        }

        for (String attrName : attributesList) {
            context.addAttributeName(attrName);
        }

        // Construire la relation d'incidence
        for (int i = 0; i < nbObjects; i++) {
            String objName = objectNames.get(i);
            Set<String> objAttributes = incidence.get(objName);

            if (objAttributes != null) {
                for (int j = 0; j < nbAttributes; j++) {
                    String attrName = attributesList.get(j);
                    if (objAttributes.contains(attrName)) {
                        context.set(i, j, true);
                    }
                }
            }
        }

        // 4) Utiliser FCA4J pour construire le treillis de concepts
        // Pour l'instant, créer un concept simple à partir des données collectées
        List<Concept> result = new ArrayList<>();

        // Pour chaque objet, créer un concept avec ses attributs
        for (String objName : objectNames) {
            // Filter out relationship concepts (contain "_to_" or "_NULL")
            if (objName.contains("_to_") || objName.contains("_NULL")) {
                System.out.println("  - Skipped relationship concept: " + objName);
                continue;
            }

            // Filter out UUID-like names
            if (objName.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                System.out.println("  - Skipped UUID concept: " + objName);
                continue;
            }

            Set<String> objAttributes = incidence.get(objName);

            // Séparer les attributs des méthodes
            Collection<String> attributes = new ArrayList<>();
            Collection<String> methods = new ArrayList<>();

            if (objAttributes != null) {
                for (String attr : objAttributes) {
                    if (attr.startsWith("m:")) {
                        methods.add(attr.substring(2));
                    } else if (!attr.startsWith("child:")) {
                        attributes.add(attr);
                    }
                }
            }

            // Skip concepts with no attributes and no methods
            if (attributes.isEmpty() && methods.isEmpty()) {
                System.out.println("  - Skipped empty concept: " + objName);
                continue;
            }

            // Créer un concept pour cet objet
            Concept c = new Concept(objName, null, attributes, methods, "Concept_" + objName);
            result.add(c);
        }

        System.out.println("FCA4JAdapter generated " + result.size() + " concepts");
        for (Concept c : result) {
            System.out.println("  - Concept: " + c.getOriginalName() +
                    " (attrs: " + c.getAttribute().size() +
                    ", methods: " + c.getMethod().size() + ")");
        }

        return result;
    } // -------------------------
      // Helpers
      // -------------------------

    private String safeGetName(Node n) {
        try {
            return n.getName();
        } catch (Exception e) {
            return null;
        }
    }

    private List<Node> collectGraphNodes(Node root) {
        List<Node> out = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        Queue<Node> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            Node cur = q.poll();
            if (cur == null)
                continue;
            int id = System.identityHashCode(cur);
            if (!seen.add(id))
                continue;
            out.add(cur);
            Collection<Node> children = cur.getChildren();
            if (children != null) {
                for (Node c : children)
                    q.add(c);
            }
        }
        return out;
    }
}
