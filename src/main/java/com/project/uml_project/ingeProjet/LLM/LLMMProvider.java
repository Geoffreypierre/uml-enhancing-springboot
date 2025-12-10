
package com.project.uml_project.ingeProjet.LLM;

public class LLMMProvider {

    private String token;
    private String model;

    public LLMMProvider(String token, String model) 
	{
		this.token = token;
		this.model = model;
    }

    public String request() {return null;};


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

	};


