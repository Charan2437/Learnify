package com.learnify.services;

import com.learnify.models.Course;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent")
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates text using Gemini API.
     */
    public String generateText(String prompt) {
        try {
            String requestBody = buildRequestBody(prompt);

            String response = fetchResponse(requestBody);
            if (response == null || response.isBlank()) {
                throw new RuntimeException("Received empty response from Gemini API");
            }

            return extractTextFromResponse(response);
        } catch (Exception e) {
            logger.error("Error in generateText: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating text: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a structured study plan for a given course using Gemini API.
     */
    public String generateCourseStudyPlan(Course course) {
        try {
            int days = course.getNumberOfDays();
            String name = course.getName();
            String learningStyle = String.join(", ", course.getPreferredLearningStyle());
            String startDate = course.getStartDate();

            // Split the study plan into smaller chunks if needed
            int batchSize = days > 10 ? 10 : days;
            StringBuilder fullStudyPlan = new StringBuilder("{ \"studyPlan\": [");

            for (int startDay = 1; startDay <= days; startDay += batchSize) {
                int endDay = Math.min(startDay + batchSize - 1, days);

                String prompt = String.format("""
                    Generate a study plan for %s from Day %d to Day %d.
                    Learning style: %s
                    Start date: %s
                    
                    Respond with ONLY a JSON object in this exact format:
                    {
                      "studyPlan": [
                        {
                          "day": "Day 1",
                          "tasks": [
                            {
                              "title": "Task Title",
                              "description": "Task Description",
                              "resourceUrls": ["url1", "url2"],
                              "status": "pending"
                            }
                          ]
                        }
                      ]
                    }
                    
                    Important:
                    1. Return ONLY the JSON object, no extra text
                    2. Ensure JSON brackets are properly closed
                    3. Include 2-3 tasks per day
                    4. Use valid resource URLs,search across web for best possible resources.If user selects visual then only search for best online courses and most viewed and liked videos.courses on youtube or any other platform.If user selects docs then search for the best blogs.If he selcts both then give both.Give atleast 4-5 best resources for his learning.
                    """,
                    name, startDay, endDay, learningStyle, startDate
                );

                String requestBody = buildRequestBody(prompt);
                String response = fetchResponse(requestBody);

                if (response == null || response.isBlank()) {
                    throw new RuntimeException("Received empty response from Gemini API");
                }

                String cleanedJson = cleanAndValidateJson(response);

                if (cleanedJson.isEmpty() || !cleanedJson.startsWith("{")) {
                    logger.warn("First attempt failed, retrying...");
                    response = fetchResponse(requestBody);
                    cleanedJson = cleanAndValidateJson(response);
                }

                // Append result to final study plan
                JsonNode jsonNode = objectMapper.readTree(cleanedJson);
                fullStudyPlan.append(jsonNode.path("studyPlan").toString().replaceFirst("\\[", "").replaceFirst("\\]$", ""));

                if (endDay < days) {
                    fullStudyPlan.append(",");
                }
            }

            fullStudyPlan.append("]}");
            return fullStudyPlan.toString();

        } catch (Exception e) {
            logger.error("Error generating study plan: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating study plan: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the JSON request body for Gemini API.
     */
    private String buildRequestBody(String prompt) {
        return String.format("""
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    }
                ]
            }""", prompt.replace("\"", "\\\""));
    }

    /**
     * Calls Gemini API and returns the raw response.
     */
    private String fetchResponse(String requestBody) {
        try {
            String response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            logger.debug("Raw Response from Gemini API: {}", response);  // Log response for debugging
            return response;
        } catch (Exception e) {
            logger.error("Error fetching response from Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Error calling Gemini API: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the generated text from the Gemini API response.
     */
    private String extractTextFromResponse(String response) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode candidates = jsonResponse.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("Invalid or missing 'candidates' in API response");
            }

            JsonNode parts = candidates.get(0).path("content").path("parts");

            if (!parts.isArray() || parts.isEmpty()) {
                throw new RuntimeException("Invalid or missing 'parts' in API response");
            }

            String generatedText = parts.get(0).path("text").asText().trim();
            return cleanMarkdown(generatedText);
        } catch (Exception e) {
            logger.error("Error parsing response JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Error extracting text from response: " + e.getMessage(), e);
        }
    }

    /**
     * Cleans and validates the JSON response.
     */
    private String cleanAndValidateJson(String response) {
        try {
            String cleanedResponse = extractTextFromResponse(response);

            int start = cleanedResponse.indexOf("{");
            int end = cleanedResponse.lastIndexOf("}") + 1;

            if (start == -1 || end <= start) {
                throw new RuntimeException("Invalid JSON structure in response");
            }

            String jsonStr = cleanedResponse.substring(start, end);
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            logger.error("Error processing JSON response: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing JSON response: " + e.getMessage());
        }
    }

    /**
     * Removes Markdown formatting from API response.
     */
    private String cleanMarkdown(String text) {
        return text.replaceAll("```json", "").replaceAll("```", "").trim();
    }
}
