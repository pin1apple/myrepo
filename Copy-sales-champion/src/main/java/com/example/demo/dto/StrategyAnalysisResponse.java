
package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAnalysisResponse {
    private String preMeetingStrategy;
    private String keyTalkingPoints;
    private String potentialObjections;
    private String recommendedApproach;
    private String successFactors;
}