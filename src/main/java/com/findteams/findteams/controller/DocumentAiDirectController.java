package com.findteams.findteams.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class DocumentAiDirectController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Client geminiClient = Client.builder()
            .apiKey("AIzaSyBQ5tkkkAtoQSlGyZNsLOjF2_okYBpXfN8") // your API key
            .build();

    private static final String PROJECT_ID = "findteams-482909";
    private static final String LOCATION = "us";
    private static final String PROCESSOR_ID = "86f22f351a3819cd";

    private static final String PARSING_API_URL =
            "https://us-documentai.googleapis.com/v1/projects/" + PROJECT_ID +
            "/locations/" + LOCATION +
            "/processors/" + PROCESSOR_ID + ":process";

    @PostMapping("/upload-resume")
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("prompt") String prompt
    ) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body("No file uploaded");

            // Send PDF to Document AI
            String base64Content = Base64.getEncoder().encodeToString(file.getBytes());
            Map<String, Object> rawDocument = Map.of(
                    "mimeType", "application/pdf",
                    "content", base64Content
            );
            Map<String, Object> requestBody = Map.of(
                    "skipHumanReview", true,
                    "rawDocument", rawDocument
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getAccessToken());

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> docAiResponse = restTemplate.postForEntity(PARSING_API_URL, requestEntity, String.class);

            if (docAiResponse.getBody() == null) return ResponseEntity.ok("Empty response from Document AI");

            // Extract plain text
            Map<?, ?> docAiJson = new ObjectMapper().readValue(docAiResponse.getBody(), Map.class);
            Map<?, ?> document = (Map<?, ?>) docAiJson.get("document");

            StringBuilder extractedTextBuilder = new StringBuilder();
            if (document != null) {
                if (document.get("text") != null) {
                    extractedTextBuilder.append(document.get("text"));
                } else if (document.get("pages") != null) {
                    List<Map<String, Object>> pages = (List<Map<String, Object>>) document.get("pages");
                    for (Map<String, Object> page : pages) {
                        if (page.get("text") != null) {
                            extractedTextBuilder.append(page.get("text")).append("\n");
                        }
                    }
                }
            }

            String extractedText = extractedTextBuilder.toString();
            if (extractedText.isBlank()) return ResponseEntity.ok("No text extracted from document");

            // Few-shot JSON example to ensure consistent output
            String finalPrompt = """
You are an automated resume evaluation system.

IMPORTANT SEPARATION OF TASKS:
- The SUMMARY must be generated ONLY from the RESUME TEXT.
- The SCORE must be calculated ONLY by comparing RESUME TEXT with JOB PROMPT.
- Do NOT mention the job prompt inside the summary.

TASKS (FOLLOW STRICTLY):

1. SUMMARY
- Write a professional summary (4–6 lines).
- Base it ONLY on the resume.
- Do NOT tailor it to the job prompt.
- Do NOT mention relevance or suitability.

2. SKILLS & TOOLS
- skill: core technical skills demonstrated in projects or experience.
- tools: frameworks, libraries, or technologies explicitly used.

3. SCORING (STRICT RULES)
- Identify REQUIRED CORE SKILLS from JOB PROMPT.
- Identify ACTUAL SKILLS from RESUME TEXT.
- Score is based ONLY on overlap.

SCORING TABLE:
- 90–100 → Strong overlap + direct domain projects
- 70–89 → Moderate overlap
- 40–69 → Weak overlap
- 10–39 → Very minimal overlap
- 0–9 → No overlap / different domain

HARD CONSTRAINTS (MANDATORY):
- If fewer than 2 required skills match → score MUST be ≤ 30
- If domain mismatch → score MUST be ≤ 20
- If zero matching skills → score MUST be ≤ 10

OUTPUT FORMAT (STRICT JSON):
ALL fields MUST be present. Missing fields are INVALID.

{
  "summary": string,
  "skill": string[],
  "tools": string[],
  "score": number
}

DO NOT include explanations.
DO NOT include markdown.
DO NOT include extra text.

JOB PROMPT:
%s

RESUME TEXT:
%s
""".formatted(prompt, extractedText);


            Content userContent = Content.builder()
                    .role("user")
                    .parts(List.of(Part.fromText(finalPrompt)))
                    .build();

            // GenerateContentConfig with deterministic output
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .temperature(0.0f)
                    .build();

            // Call Gemini
            GenerateContentResponse geminiResponse = geminiClient.models.generateContent(
                    "gemini-2.5-flash",
                    List.of(userContent),
                    config
            );

            String geminiOutput = geminiResponse.text().trim();

            // Optional: validate JSON
            try {
                Map<?, ?> jsonOutput = new ObjectMapper().readValue(geminiOutput, Map.class);
                return ResponseEntity.ok(new ObjectMapper().writeValueAsString(jsonOutput));
            } catch (Exception ex) {
                // fallback: return raw text if parsing fails
                return ResponseEntity.ok(geminiOutput);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    private String getAccessToken() throws Exception {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(
                        "C:\\Users\\riddh\\Downloads\\findteams-482909-3c8e60c5f1b4.json"
                ))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
