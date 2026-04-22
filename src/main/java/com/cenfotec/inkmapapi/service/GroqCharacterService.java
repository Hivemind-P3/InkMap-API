package com.cenfotec.inkmapapi.service;

import com.cenfotec.inkmapapi.dto.CreateStoryCharacterRequestDTO;
import com.cenfotec.inkmapapi.models.enums.Gender;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class GroqCharacterService {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GroqCharacterService(ObjectMapper objectMapper,
                                 @Value("${groq.api.key}") String apiKey,
                                 @Value("${groq.model}") String model) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.create();
    }

    public CreateStoryCharacterRequestDTO generate(String context, String instructions) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", buildSystemPrompt(context)),
                        Map.of("role", "user", "content", instructions)
                ),
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.7,
                "max_tokens", 512
        );

        try {
            String responseBody = restClient.post()
                    .uri(GROQ_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseResponse(responseBody);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI service unavailable");
        }
    }

    private String buildSystemPrompt(String context) {
        return """
                Eres un asistente de escritura creativa especializado en generar personajes de ficción.

                CONTEXTO DEL PROYECTO:
                %s

                REGLAS:
                1. Si el contexto del proyecto es rico y detallado, genera un personaje coherente con ese universo. No repitas nombres existentes.
                2. Si el contexto es pobre o genérico (proyectos de prueba, títulos como TEST, sin descripción real), genera el personaje basándote principalmente en las instrucciones del usuario.
                3. Si las instrucciones del usuario son demasiado vagas para crear un personaje con sentido (por ejemplo: "crea un personaje", "haz algo", sin ningún detalle sobre rol, rasgos o historia), devuelve ÚNICAMENTE este JSON: {"error": "The instructions are insufficient. Please describe the character’s role, personality, background, or traits."}
                4. Si puedes generar el personaje (ya sea por contexto rico o por instrucciones suficientes), devuelve ÚNICAMENTE el JSON del personaje.

                FORMATO DEL PERSONAJE (cuando sí puedes generarlo):
                - name (string, requerido): nombre del personaje
                - role (string): rol narrativo del personaje
                - description (string): descripción del personaje
                - age (integer o null): edad del personaje
                - gender (string, requerido): exactamente "MALE", "FEMALE" o "OTHER"
                - race (string): raza o especie del personaje

                No incluyas texto fuera del JSON.
                """.formatted(context);
    }

    public List<CreateStoryCharacterRequestDTO> generateSuggestions(String context, String instructions) {
        String userMessage = (instructions == null || instructions.isBlank())
                ? "Sin instrucciones adicionales. Sugiere personajes que enriquezcan la historia basándote en el contexto."
                : instructions;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", buildSuggestionsPrompt(context)),
                        Map.of("role", "user", "content", userMessage)
                ),
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.9,
                "max_tokens", 1024
        );

        try {
            String responseBody = restClient.post()
                    .uri(GROQ_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseSuggestionsResponse(responseBody);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI service unavailable");
        }
    }

    private String buildSuggestionsPrompt(String context) {
        return """
                Eres un asistente de escritura creativa especializado en generar personajes de ficción.

                CONTEXTO DEL PROYECTO:
                %s

                Genera exactamente 3 sugerencias de personajes distintos y coherentes con el universo narrativo del proyecto.
                No repitas nombres de personajes existentes.
                Si el contexto es pobre o genérico, crea personajes variados e interesantes basándote en las instrucciones del usuario.

                Devuelve ÚNICAMENTE este JSON:
                {"suggestions": [<personaje1>, <personaje2>, <personaje3>]}

                Cada personaje debe tener:
                - name (string, requerido): nombre del personaje
                - role (string): rol narrativo del personaje
                - description (string): descripción del personaje
                - age (integer o null): edad del personaje
                - gender (string, requerido): exactamente "MALE", "FEMALE" o "OTHER"
                - race (string): raza o especie del personaje

                No incluyas texto fuera del JSON.
                """.formatted(context);
    }

    private List<CreateStoryCharacterRequestDTO> parseSuggestionsResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            JsonNode body = objectMapper.readTree(content);

            JsonNode suggestions = body.path("suggestions");
            if (suggestions.isMissingNode() || !suggestions.isArray() || suggestions.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "AI response did not return a suggestions array");
            }

            List<CreateStoryCharacterRequestDTO> result = new java.util.ArrayList<>();
            for (JsonNode node : suggestions) {
                result.add(parseCharacterNode(node));
            }
            return result;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Could not parse AI suggestions response");
        }
    }

    private CreateStoryCharacterRequestDTO parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            JsonNode character = objectMapper.readTree(content);

            String errorMsg = character.path("error").asText(null);
            if (errorMsg != null && !errorMsg.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, errorMsg);
            }

            return parseCharacterNode(character);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Could not parse AI response");
        }
    }

    private CreateStoryCharacterRequestDTO parseCharacterNode(JsonNode character) {
        String name = character.path("name").asText(null);
        String genderStr = character.path("gender").asText(null);

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "AI response missing required field: name");
        }
        if (genderStr == null || genderStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "AI response missing required field: gender");
        }

        Gender gender;
        try {
            gender = Gender.valueOf(genderStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "AI response contains invalid gender value: " + genderStr);
        }

        JsonNode ageNode = character.path("age");
        Integer age = (ageNode.isMissingNode() || ageNode.isNull()) ? null : ageNode.asInt();

        CreateStoryCharacterRequestDTO dto = new CreateStoryCharacterRequestDTO();
        dto.setName(name.trim());
        dto.setRole(character.path("role").asText(null));
        dto.setDescription(character.path("description").asText(null));
        dto.setAge(age);
        dto.setGender(gender);
        dto.setRace(character.path("race").asText(null));
        return dto;
    }
}
