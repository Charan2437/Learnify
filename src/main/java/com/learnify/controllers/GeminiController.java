package com.learnify.controllers;

import com.learnify.services.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateWithGet(@RequestParam String prompt) {
        try {
            String response = geminiService.generateText(prompt);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateWithPost(@RequestBody GenerateRequest request) {
        try {
            String response = geminiService.generateText(request.getPrompt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}

class GenerateRequest {
    private String prompt;

    // Getters and setters
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}