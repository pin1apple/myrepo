package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🏆 复制销冠 AI智能体启动成功！");
        System.out.println("📱 前端页面: http://localhost:8080");
        System.out.println("🔧 API文档: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
}
