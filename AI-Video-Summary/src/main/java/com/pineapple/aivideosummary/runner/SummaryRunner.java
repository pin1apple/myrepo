package com.pineapple.aivideosummary.runner;

import com.pineapple.aivideosummary.service.SummarizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryRunner implements CommandLineRunner {

    private final SummarizerService summarizerService;

    @Override
    public void run(String... args) {
        log.info("🚀 AI视频总结工具启动");
        log.info("========================================");

        try {
            summarizerService.generateSummary();
            log.info("========================================");
            log.info("✨ 所有任务完成！");
        } catch (Exception e) {
            log.error("❌ 任务执行失败", e);
        }

        System.exit(0);
    }
}