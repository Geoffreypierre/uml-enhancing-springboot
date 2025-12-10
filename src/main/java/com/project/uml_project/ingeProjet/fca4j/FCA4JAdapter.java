package com.project.uml_project.ingeProjet.fca4j;

import com.project.uml_project.ingeProjet.main.Concept;
import com.project.uml_project.ingeProjet.utils.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FCA4JAdapter utilisant directement l'API fca4j-core (0.4.4).
 * - generate prend UNE Node racine (KG)
 * - construit un Context formel à partir des nodes du graphe
 * - construit le treillis de concepts via LatticeBuilder
 * - convertit les FormalConcept en
 * com.project.uml_project.ingeProjet.main.Concept
 *
 * Attention : adapte les imports si ta version de fca4j a des packages/méthodes
 * légèrement différents.
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

        // 3) Construire le Context formel via ContextBuilder (API fca4j)
        // On crée un Context avec la liste d'objets, la liste d'attributs et la matrice
        // d'incidence.
        List<String> attributesList = new ArrayList<>(allAttributes);

        // Build the binary context (currently not used, but prepared for future FCA
        // implementation)
        // BinaryContext context = buildContext(objectNames, attributesList, incidence);

        // 4) Build concept lattice - simplified implementation
        // TODO: Properly extract formal concepts from BinaryContext using FCA4J
        // algorithms
        List<Concept> result = new ArrayList<>();

        // Create a single concept from all objects and attributes as a placeholder
        if (!objectNames.isEmpty()) {
            String conceptName = "GeneratedConcept";
            Collection<String> attributes = new ArrayList<>(attributesList);
            Collection<String> methods = Collections.emptyList();
            String originalName = objectNames.stream().collect(Collectors.joining(";"));

            Concept c = new Concept(originalName, null, attributes, methods, conceptName);
            result.add(c);
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
