package com.project.uml_project.ingeProjet.utils;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.SourceStringReader;

public class Parser {

    private String puml;

    public Parser() {
    }

    public void setPuml(String puml) {
        this.puml = puml;
    }

    public boolean validate() {
        if (puml == null || puml.isEmpty()) {
            return false;
        }

        try {
            SourceStringReader reader = new SourceStringReader(puml);
            if (reader.getBlocks().isEmpty()) {
                return false;
            }

            BlockUml block = reader.getBlocks().get(0);
            // Check if it's an error
            if (block.getDiagram() instanceof ErrorUml) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Diagram parse() {
        if (!this.validate()) {
            return null;
        }
        // Parse le PUML pour en faire un diagramme
        SourceStringReader reader = new SourceStringReader(puml);
        return new Diagram(reader.getBlocks().get(0).getDiagram());
    }

}
