package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionAnalysisResponse {
    private Long sessionId;
    private String sessionName;
    private String customerName;
    private LocalDateTime sessionDate;
    private String transcriptionText;
    private String conversationAnalysis;
    private String improvementSuggestions;
    private Double overallScore;
    private String keyStrengths;
    private String areasForImprovement;
    private String reportStatus;
}