package com.project.uml_project.ingeProjet.LLM;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageParam;
import com.openai.models.chat.completions.ChatCompletionUserMessageParam;

import java.util.List;

public class LLMProvider {

    private String model;
    private OpenAIClient client;

    public LLMProvider(String token, String model) {
        this.model = model;
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(token)
                .build();
    }

    public String request(String prompt) throws Exception {
        try {
            ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                    .content(ChatCompletionUserMessageParam.Content.ofText(prompt))
                    .build();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(model)
                    .messages(List.of(ChatCompletionMessageParam.ofUser(userMessage)))
                    .build();

            ChatCompletion response = client.chat().completions().create(params);
            return response.choices().get(0).message().content().orElse("");
        } catch (Exception e) {
            throw new Exception("Failed to make LLM request: " + e.getMessage(), e);
        }
    }

}