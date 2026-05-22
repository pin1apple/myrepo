
package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.RecordingUploadRequest;
import com.example.demo.dto.SessionAnalysisResponse;
import com.example.demo.entity.SalesSession;
import com.example.demo.entity.AnalysisReport;
import com.example.demo.repository.SalesSessionRepository;
import com.example.demo.repository.AnalysisReportRepository;
import com.example.demo.service.SalesAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "*")
@Slf4j
public class SessionController {

    @Autowired
    private SalesSessionRepository sessionRepository;

    @Autowired
    private AnalysisReportRepository reportRepository;

    @Autowired
    private SalesAiService salesAiService;

    private static final String RECORDING_DIR = "./recordings";

    @PostMapping("/upload")
    public ApiResponse<SalesSession> uploadRecording(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("sessionName") String sessionName,
            @RequestParam(value = "customerName", required = false) String customerName) {
        try {
            File dir = new File(RECORDING_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + "_" + audioFile.getOriginalFilename();
            Path filePath = Paths.get(RECORDING_DIR, fileName);
            Files.write(filePath, audioFile.getBytes());

            SalesSession session = new SalesSession();
            session.setSessionName(sessionName);
            session.setCustomerName(customerName);
            session.setRecordingPath(filePath.toString());
            session.setSessionDate(LocalDateTime.now());
            session.setStatus("UPLOADED");

            session = sessionRepository.save(session);

            return ApiResponse.success(session);
        } catch (Exception e) {
            log.error("上传录音失败", e);
            return ApiResponse.error("上传录音失败: " + e.getMessage());
        }
    }

    @PostMapping("/transcribe/{sessionId}")
    public ApiResponse<SalesSession> transcribeRecording(@PathVariable Long sessionId) {
        try {
            SalesSession session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("会话不存在"));

            String mockTranscription = "这是模拟的转录文本。在实际应用中，这里会集成语音识别API（如讯飞、百度语音等）将录音转换为文字。销售人员：您好，很高兴今天能与您见面。客户：你好，我对你们的产品很感兴趣...";

            session.setTranscriptionText(mockTranscription);
            session.setStatus("TRANSCRIBED");
            session = sessionRepository.save(session);

            return ApiResponse.success(session);
        } catch (Exception e) {
            return ApiResponse.error("转录失败: " + e.getMessage());
        }
    }

    @PostMapping("/analyze/{sessionId}")
    public ApiResponse<SessionAnalysisResponse> analyzeSession(@PathVariable Long sessionId) {
        try {
            SalesSession session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("会话不存在"));

            if (session.getTranscriptionText() == null || session.getTranscriptionText().isEmpty()) {
                return ApiResponse.error("请先进行录音转录");
            }

            String analysis = salesAiService.analyzeConversation(session.getTranscriptionText());
            String suggestions = salesAiService.generateImprovementSuggestions(analysis);

            AnalysisReport report = new AnalysisReport();
            report.setSessionId(sessionId);
            report.setConversationAnalysis(analysis);
            report.setImprovementSuggestions(suggestions);
            report.setOverallScore(Math.random() * 40 + 60);
            report.setKeyStrengths("1. 良好的开场白\n2. 积极倾听客户需求\n3. 专业的产品知识");
            report.setAreasForImprovement("1. 加强异议处理技巧\n2. 提升成交闭环能力\n3. 优化提问策略");

            report = reportRepository.save(report);

            session.setStatus("ANALYZED");
            sessionRepository.save(session);

            SessionAnalysisResponse response = new SessionAnalysisResponse();
            response.setSessionId(session.getId());
            response.setSessionName(session.getSessionName());
            response.setCustomerName(session.getCustomerName());
            response.setSessionDate(session.getSessionDate());
            response.setTranscriptionText(session.getTranscriptionText());
            response.setConversationAnalysis(report.getConversationAnalysis());
            response.setImprovementSuggestions(report.getImprovementSuggestions());
            response.setOverallScore(report.getOverallScore());
            response.setKeyStrengths(report.getKeyStrengths());
            response.setAreasForImprovement(report.getAreasForImprovement());
            response.setReportStatus("COMPLETED");

            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("分析会话失败", e);
            return ApiResponse.error("分析失败: " + e.getMessage());
        }
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<SessionAnalysisResponse> getSessionDetail(@PathVariable Long sessionId) {
        try {
            SalesSession session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("会话不存在"));

            AnalysisReport report = reportRepository.findBySessionId(sessionId);

            SessionAnalysisResponse response = new SessionAnalysisResponse();
            response.setSessionId(session.getId());
            response.setSessionName(session.getSessionName());
            response.setCustomerName(session.getCustomerName());
            response.setSessionDate(session.getSessionDate());
            response.setTranscriptionText(session.getTranscriptionText());

            if (report != null) {
                response.setConversationAnalysis(report.getConversationAnalysis());
                response.setImprovementSuggestions(report.getImprovementSuggestions());
                response.setOverallScore(report.getOverallScore());
                response.setKeyStrengths(report.getKeyStrengths());
                response.setAreasForImprovement(report.getAreasForImprovement());
                response.setReportStatus("COMPLETED");
            } else {
                response.setReportStatus("PENDING");
            }

            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("获取会话详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ApiResponse<List<SalesSession>> listSessions(
            @RequestParam(value = "status", required = false) String status) {
        try {
            List<SalesSession> sessions;
            if (status != null && !status.isEmpty()) {
                sessions = sessionRepository.findByStatus(status);
            } else {
                sessions = sessionRepository.findAll();
            }
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("获取会话列表失败: " + e.getMessage());
        }
    }
}