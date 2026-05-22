
package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.StrategyAnalysisRequest;
import com.example.demo.dto.StrategyAnalysisResponse;
import com.example.demo.service.SalesAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin(origins = "*")
@Slf4j
public class StrategyController {

    @Autowired
    private SalesAiService salesAiService;

    @PostMapping("/analyze")
    public ApiResponse<StrategyAnalysisResponse> analyzeStrategy(@RequestBody StrategyAnalysisRequest request) {
        try {
            StrategyAnalysisResponse response = salesAiService.analyzePreMeetingStrategy(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("策略分析失败", e);
            return ApiResponse.error("策略分析失败: " + e.getMessage());
        }
    }

    @PostMapping("/chat")
    public ApiResponse<String> chatWithAI(@RequestBody ChatRequest request) {
        try {
            String response = salesAiService.queryWithRag(request.getMessage());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("AI对话失败", e);
            return ApiResponse.error("AI对话失败: " + e.getMessage());
        }
    }

    static class ChatRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}