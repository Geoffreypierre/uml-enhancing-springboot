package com.project.uml_project.ingeProjet.LLM;

public class LLMProvider {

    private String token;
    private String model;

    public LLMProvider(String token, String model)
	{
		this.token = token;
		this.model = model;
    }

    public String request(String prompt) {
        //Appeller le LLM avec le prompt et retourner la réponse
        return null;}

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

	}


