


package com.pineapple.aivideosummary.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pineapple.aivideosummary.config.AIConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final AIConfig aiConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chat(String prompt) {
        try {
            log.info("准备调用通义千问 API");
            log.info("API URL: {}", aiConfig.getApiUrl());
            log.info("Model: {}", aiConfig.getModel());

            // 通义千问的请求格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());

            Map<String, Object> input = new HashMap<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            input.put("messages", new Object[]{message});
            requestBody.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", aiConfig.getTemperature());
            requestBody.put("parameters", parameters);

            String response = webClient.post()
                    .uri(aiConfig.getApiUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + aiConfig.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("API响应: {}", response.substring(0, Math.min(200, response.length())));

            JsonNode jsonNode = objectMapper.readTree(response);

            // 检查是否有错误
            if (jsonNode.has("code")) {
                String errorCode = jsonNode.get("code").asText();
                String errorMessage = jsonNode.get("message").asText();
                throw new RuntimeException("通义千问API错误: " + errorCode + " - " + errorMessage);
            }

            // 通义千问的响应格式
            String content = jsonNode.get("output")
                    .get("text")
                    .asText();

            log.info("AI响应成功，内容长度: {}", content.length());
            return content;

        } catch (Exception e) {
            log.error("调用AI API失败", e);
            throw new RuntimeException("调用AI API失败: " + e.getMessage(), e);
        }
    }
}