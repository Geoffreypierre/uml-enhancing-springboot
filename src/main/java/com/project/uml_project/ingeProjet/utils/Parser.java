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
    public boolean validate(){

        return false;}
    public Diagram parse() {
        if (!this.validate()) {
            return null;
        }
        //Parse le PUML pour en faire un diagramme
        SourceStringReader reader = new SourceStringReader(puml);
        return new Diagram(reader.getBlocks().getFirst().getDiagram());}

}

