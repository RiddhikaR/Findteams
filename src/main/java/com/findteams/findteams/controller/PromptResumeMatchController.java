package com.findteams.findteams.controller;

import com.findteams.findteams.service.ResumeMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prompt-match")
@RequiredArgsConstructor
public class PromptResumeMatchController {

    private final ResumeMatchingService matchingService;

    @PostMapping
    public List<Map<String, Object>> matchPrompt(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        return matchingService.matchResumesFromPrompt(prompt);
    }
}
