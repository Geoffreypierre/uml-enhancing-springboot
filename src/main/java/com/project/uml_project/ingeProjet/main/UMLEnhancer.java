package com.project.uml_project.ingeProjet.main;

import com.project.uml_project.ingeProjet.main.UMLEnhancer;
import com.project.uml_project.ingeProjet.fca4j.FCA4JAdapter;
import com.project.uml_project.ingeProjet.utils.Parser;






public class UMLEnhancer {

    private Parser Parser;
    private FCA4JAdapter dca4jAdapter;
    private UMLEnhancer pumlBuilder;

    public UMLEnhancer(Parser Parser, FCA4JAdapter dca4jAdapter, UMLEnhancer pumlBuilder) 
	{
		this.Parser = Parser;
		this.dca4jAdapter = dca4jAdapter;
		this.pumlBuilder = pumlBuilder;
    }

    public void init() {};
    public void exec() {};


    public Parser getParser() {
        return this.Parser;
    }

    public void setParser(Parser Parser) {
        this.Parser = Parser;
    }
    public FCA4JAdapter getDca4jAdapter() {
        return this.dca4jAdapter;
    }

    public void setDca4jAdapter(FCA4JAdapter dca4jAdapter) {
        this.dca4jAdapter = dca4jAdapter;
    }
    public UMLEnhancer getPumlBuilder() {
        return this.pumlBuilder;
    }

    public void setPumlBuilder(UMLEnhancer pumlBuilder) {
        this.pumlBuilder = pumlBuilder;
    }

	};


