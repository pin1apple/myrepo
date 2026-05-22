
package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAnalysisRequest {
    private String customerInfo;
    private String productInfo;
    private String meetingObjective;
    private String historicalContext;
}