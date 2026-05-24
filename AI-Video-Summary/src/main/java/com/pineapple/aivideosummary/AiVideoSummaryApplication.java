

package com.pineapple.aivideosummary;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pineapple.aivideosummary.config.AIConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
public class AiVideoSummaryApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AiVideoSummaryApplication.class, args);

        // 测试API密钥和余额
        testApiKeyAndBalance(context);
    }

    private static void testApiKeyAndBalance(ConfigurableApplicationContext context) {
        try {
            System.out.println("\n========================================");
            System.out.println("开始测试 DeepSeek API...");
            System.out.println("========================================\n");

            AIConfig aiConfig = context.getBean(AIConfig.class);
            WebClient webClient = context.getBean(WebClient.class);
            ObjectMapper objectMapper = new ObjectMapper();

            System.out.println("API Key: " + aiConfig.getApiKey().substring(0, 10) + "...");
            System.out.println("API URL: " + aiConfig.getApiUrl());
            System.out.println("Model: " + aiConfig.getModel());
            System.out.println("\n----------------------------------------");

            // 发送测试请求
            System.out.println("\n发送测试请求...");

            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("model", aiConfig.getModel());
            requestBody.put("temperature", 0.2);
            requestBody.put("max_tokens", 50);

            java.util.Map<String, String> message = new java.util.HashMap<>();
            message.put("role", "user");
            message.put("content", "你好");

            requestBody.put("messages", java.util.List.of(message));

            String response = webClient.post()
                    .uri(aiConfig.getApiUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + aiConfig.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(response);

            // 解析响应
            if (jsonNode.has("choices")) {
                String content = jsonNode.get("choices")
                        .get(0)
                        .get("message")
                        .get("content")
                        .asText();

                System.out.println("✅ API调用成功！");
                System.out.println("AI回复: " + content);

                // 显示使用的tokens
                if (jsonNode.has("usage")) {
                    JsonNode usage = jsonNode.get("usage");
                    int promptTokens = usage.get("prompt_tokens").asInt();
                    int completionTokens = usage.get("completion_tokens").asInt();
                    int totalTokens = usage.get("total_tokens").asInt();

                    System.out.println("\n----------------------------------------");
                    System.out.println("Token使用情况:");
                    System.out.println("  输入Tokens: " + promptTokens);
                    System.out.println("  输出Tokens: " + completionTokens);
                    System.out.println("  总计Tokens: " + totalTokens);
                }

                System.out.println("\n========================================");
                System.out.println("✅ API密钥有效，可以正常使用！");
                System.out.println("========================================\n");
            }

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            System.err.println("\n❌ API调用失败！");
            System.err.println("HTTP状态码: " + e.getStatusCode());
            System.err.println("错误信息: " + e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 402) {
                System.err.println("\n⚠️  原因: 账户余额不足或免费额度已用完");
                System.err.println("\n解决方案:");
                System.err.println("1. 访问 https://platform.deepseek.com 查看账户状态");
                System.err.println("2. 确认是否还有免费额度");
                System.err.println("3. 如需继续使用，请充值或注册新账户");
            } else if (e.getStatusCode().value() == 401) {
                System.err.println("\n⚠️  原因: API密钥无效或过期");
                System.err.println("\n解决方案:");
                System.err.println("1. 检查API密钥是否正确");
                System.err.println("2. 重新生成新的API密钥");
            }
            System.err.println("\n========================================\n");

        } catch (Exception e) {
            System.err.println("\n❌ 测试失败！");
            System.err.println("错误类型: " + e.getClass().getSimpleName());
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n========================================\n");
        }

        System.exit(0);
    }

}