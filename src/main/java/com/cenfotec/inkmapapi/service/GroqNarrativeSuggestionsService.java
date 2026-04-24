package com.cenfotec.inkmapapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Service
public class GroqNarrativeSuggestionsService {
    @Value("${groq.model}")
    private String model;

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.groq.com/openai/v1";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getSuggestions(String narrativeContext, String additionalInstructions) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt = """
                You are a narrative analysis assistant. Analyze the provided story chapters and give suggestions focusing on:
                1. Plot holes or gaps in the narrative
                2. Ways to develop and implement the main themes
                3. Contradictions between chapters
                Be specific, reference the actual content, and be constructive.
                Respond in the same language as the narrative content.
                """;

        String userPrompt = narrativeContext;
        if(additionalInstructions != null && !additionalInstructions.isBlank()) {
            userPrompt += "\n\nAdditional instructions: " + additionalInstructions;
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.7,
                "max_tokens", 1024
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                API_URL + "/chat/completions", request, Map.class
        );

        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
