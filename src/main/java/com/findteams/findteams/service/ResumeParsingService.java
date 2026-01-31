package com.findteams.findteams.service;

import com.findteams.findteams.model.StudentResume;
import com.findteams.findteams.repository.StudentResumeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.Base64;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeParsingService {

    private final StudentResumeRepo resumeRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Client geminiClient = Client.builder()
            .apiKey("AIzaSyBQ5tkkkAtoQSlGyZNsLOjF2_okYBpXfN8") // Replace with your Gemini key
            .build();

    private static final String PROJECT_ID = "findteams-482909";
    private static final String LOCATION = "us";
    private static final String PROCESSOR_ID = "86f22f351a3819cd";

    private static final String PARSING_API_URL =
            "https://us-documentai.googleapis.com/v1/projects/" + PROJECT_ID +
            "/locations/" + LOCATION +
            "/processors/" + PROCESSOR_ID + ":process";

    @Transactional
    public void parseAndSaveSummary(StudentResume studentResume, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                System.err.println("⚠️ Empty file uploaded");
                return;
            }

            // 1️⃣ Send PDF to Document AI
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

            if (docAiResponse.getBody() == null) {
                System.err.println("⚠️ Empty response from Document AI");
                return;
            }

            // 2️⃣ Extract plain text from Document AI
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
            if (extractedText.isBlank()) {
                System.err.println("⚠️ No text extracted from document");
                return;
            }

            // 3️⃣ Call Gemini to get SUMMARY only
            String finalPrompt = """
You are a resume summarization system.
TASK: Write a professional summary (4–6 lines) based ONLY on the resume text.
Do NOT include skills, score, or job prompt information.
OUTPUT ONLY the summary string.
RESUME TEXT:
%s
""".formatted(extractedText);

            Content userContent = Content.builder()
                    .role("user")
                    .parts(List.of(Part.fromText(finalPrompt)))
                    .build();

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .temperature(0.0f)
                    .build();

           // Call Gemini AI summary
GenerateContentResponse geminiResponse = geminiClient.models.generateContent(
        "gemini-2.5-flash",
        List.of(userContent),
        config
);

// 1️⃣ Print the raw Gemini response object
System.out.println("📌 Raw Gemini response object: " + geminiResponse);

// 2️⃣ Print the raw text directly from Gemini before trimming
String rawText = geminiResponse.text();
System.out.println("📌 Raw Gemini response text: " + rawText);

// 3️⃣ Trim and save summary
String summary = rawText.trim();
System.out.println("✅ Summary after trim: " + summary);

studentResume.setSummary(summary);
resumeRepo.saveAndFlush(studentResume);
System.out.println("💾 Summary saved for student: " + studentResume.getStudent().getName());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAccessToken() throws Exception {
        var credentials = com.google.auth.oauth2.GoogleCredentials
                .fromStream(new FileInputStream("C:\\Users\\riddh\\Downloads\\findteams-482909-3c8e60c5f1b4.json"))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
