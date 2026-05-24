

package com.pineapple.aivideosummary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizerService {

    private final AIClient aiClient;
    private final PromptTemplates promptTemplates;

    @Value("${subtitles.folder}")
    private String subtitlesFolder;

    @Value("${output.filename}")
    private String outputFilename;

    public void generateSummary() {
        try {
            log.info("开始处理字幕文件...");

            Path folderPath = Paths.get(subtitlesFolder);
            if (!Files.exists(folderPath)) {
                log.error("字幕文件夹不存在: {}", subtitlesFolder);
                return;
            }

            List<Path> subtitleFiles = new ArrayList<>();
            try (Stream<Path> paths = Files.walk(folderPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".txt"))
                        .sorted(this::compareByPartNumber)
                        .forEach(subtitleFiles::add);
            }

            if (subtitleFiles.isEmpty()) {
                log.warn("未找到任何字幕文件");
                return;
            }

            log.info("找到 {} 个字幕文件", subtitleFiles.size());

            // 打印文件顺序（调试用）
            for (int i = 0; i < subtitleFiles.size(); i++) {
                log.info("文件 {}: {}", i + 1, subtitleFiles.get(i).getFileName());
            }

            // 分批处理：每8个视频一组
            int batchSize = 8;
            List<String> batchSummaries = new ArrayList<>();
            int totalBatches = (subtitleFiles.size() + batchSize - 1) / batchSize;

            for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
                int startIndex = batchIndex * batchSize;
                int endIndex = Math.min(startIndex + batchSize, subtitleFiles.size());

                List<Path> batch = subtitleFiles.subList(startIndex, endIndex);

                log.info("处理第 {} 批/共{}批: 文件 {}-{}",
                        batchIndex + 1,
                        totalBatches,
                        getPartNumber(batch.get(0)),
                        getPartNumber(batch.get(batch.size() - 1)));

                StringBuilder batchContent = new StringBuilder();
                for (Path filePath : batch) {
                    String content = Files.readString(filePath);
                    String videoTitle = extractVideoTitle(filePath.getFileName().toString());
                    batchContent.append("【").append(videoTitle).append("】\n");
                    batchContent.append(content).append("\n\n");
                }

                String batchPrompt = promptTemplates.buildBatchSummaryPrompt(batchContent.toString());
                String batchSummary = aiClient.chat(batchPrompt);
                batchSummaries.add(batchSummary);

                log.info("第 {} 批处理完成", batchIndex + 1);
                Thread.sleep(2000);
            }

            log.info("所有批次处理完成，正在整合最终文档...");

            // 整合所有批次的总结
            StringBuilder allBatches = new StringBuilder();
            for (int i = 0; i < batchSummaries.size(); i++) {
                allBatches.append("【第").append(i + 1).append("部分】\n");
                allBatches.append(batchSummaries.get(i)).append("\n\n");
            }

            String finalPrompt = promptTemplates.buildFinalIntegrationPrompt(allBatches.toString());
            String finalSummary = aiClient.chat(finalPrompt);

            Files.writeString(Paths.get(outputFilename), finalSummary);

            log.info("✅ 学习笔记生成成功: {}", outputFilename);

        } catch (Exception e) {
            log.error("生成学习笔记失败", e);
            e.printStackTrace();
            throw new RuntimeException("生成学习笔记失败", e);
        }
    }

    private int compareByPartNumber(Path p1, Path p2) {
        int num1 = extractPartNumber(p1.getFileName().toString());
        int num2 = extractPartNumber(p2.getFileName().toString());
        return Integer.compare(num1, num2);
    }

    private int extractPartNumber(String fileName) {
        try {
            int start = fileName.indexOf("- P") + 3;
            int end = fileName.indexOf("【", start);
            if (end == -1) {
                end = fileName.indexOf("-", start);
                if (end == -1) end = fileName.length();
            }
            String numStr = fileName.substring(start, end).trim();
            return Integer.parseInt(numStr);
        } catch (Exception e) {
            log.warn("无法解析文件名中的序号: {}", fileName);
            return 0;
        }
    }

    private String getPartNumber(Path filePath) {
        int num = extractPartNumber(filePath.getFileName().toString());
        return "P" + num;
    }

    private String extractVideoTitle(String fileName) {
        int pIndex = fileName.indexOf("- P");
        if (pIndex != -1) {
            return fileName.substring(0, pIndex).trim();
        }
        int bracketIndex = fileName.indexOf("—【");
        if (bracketIndex != -1) {
            return fileName.substring(0, bracketIndex).trim();
        }
        return fileName.replace(".txt", "").trim();
    }
}