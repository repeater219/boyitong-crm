package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.service.AiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String message = body != null ? body.get("message") : null;
        if (message == null || message.isBlank()) {
            return Result.error(400, "消息不能为空");
        }
        String reply = aiService.chat(message);
        return Result.success(Map.of("reply", reply));
    }

    @PostMapping("/generate-followup")
    public Result<Map<String, String>> generateFollowUp(@RequestBody Map<String, String> body) {
        String keywords = body.get("keywords");
        if (keywords == null || keywords.isBlank()) {
            return Result.error(400, "关键词不能为空");
        }
        String content = aiService.generateFollowUp(keywords);
        return Result.success(Map.of("content", content));
    }
}