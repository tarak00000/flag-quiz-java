package com.example.flagquiz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlagQuizApplication implements CommandLineRunner {
    
    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    public static void main(String[] args) {
        SpringApplication.run(FlagQuizApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 国旗クイズゲーム 起動 ===");
        System.out.println("ポート: http://localhost:8080");
        
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.contains("your-actual-gemini-api-key-here")) {
            System.out.println("Gemini API: 設定済み (キー: " + geminiApiKey.substring(0, Math.min(10, geminiApiKey.length())) + "...)");
        } else {
            System.out.println("Gemini API: 未設定 (フォールバック機能を使用)");
        }
        System.out.println("===============================");
    }
}