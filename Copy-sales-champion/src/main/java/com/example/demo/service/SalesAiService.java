package com.example.demo.service;


import com.example.demo.dto.*;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SalesAiService {

    @Autowired
    private OpenAiChatModel chatModel;

    @Autowired
    private RagService ragService;

    public StrategyAnalysisResponse analyzePreMeetingStrategy(StrategyAnalysisRequest request) {
        String prompt = String.format(
                "作为顶级销售专家，请根据以下信息制定面销策略：\n\n" +
                        "客户信息：%s\n" +
                        "产品信息：%s\n" +
                        "会议目标：%s\n" +
                        "历史背景：%s\n\n" +
                        "请提供：\n" +
                        "1. 会前策略分析\n" +
                        "2. 关键话术要点\n" +
                        "3. 可能遇到的异议及应对\n" +
                        "4. 推荐的沟通方式\n" +
                        "5. 成功关键因素",
                request.getCustomerInfo(),
                request.getProductInfo(),
                request.getMeetingObjective(),
                request.getHistoricalContext()
        );

        String response = ragService.queryWithRag(prompt);

        StrategyAnalysisResponse result = new StrategyAnalysisResponse();
        result.setPreMeetingStrategy(extractSection(response, "会前策略"));
        result.setKeyTalkingPoints(extractSection(response, "话术要点"));
        result.setPotentialObjections(extractSection(response, "异议"));
        result.setRecommendedApproach(extractSection(response, "沟通方式"));
        result.setSuccessFactors(extractSection(response, "成功关键"));

        return result;
    }

    public String analyzeConversation(String transcription) {
        String prompt = String.format(
                "作为销售教练，请对以下销售对话进行深度分析：\n\n%s\n\n" +
                        "请评估：\n" +
                        "1. 销售人员的专业表现\n" +
                        "2. 客户需求挖掘程度\n" +
                        "3. 异议处理技巧\n" +
                        "4. 成交推进能力\n" +
                        "5. 改进建议",
                transcription
        );

        return ragService.queryWithRag(prompt);
    }

    public String generateImprovementSuggestions(String analysis) {
        String prompt = String.format(
                "基于以下销售对话分析结果，提供具体可执行的改进建议：\n\n%s\n\n" +
                        "请从以下维度给出建议：\n" +
                        "1. 沟通技巧提升\n" +
                        "2. 产品知识加强\n" +
                        "3. 客户心理把握\n" +
                        "4. 谈判策略优化\n" +
                        "5. 实战训练方法",
                analysis
        );

        return ragService.queryWithRag(prompt);
    }

    public String queryWithRag(String question) {
        return ragService.queryWithRag(question);
    }

    private String extractSection(String text, String sectionName) {
        int startIndex = text.indexOf(sectionName);
        if (startIndex == -1) {
            return "";
        }

        int endIndex = text.indexOf("\n\n", startIndex + sectionName.length());
        if (endIndex == -1) {
            endIndex = text.length();
        }

        return text.substring(startIndex + sectionName.length(), endIndex).trim();
    }


}