package com.example.flagquiz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini AIサービス - 国旗クイズゲームでのAI機能を提供
 * 質問検証、回答、ヒント生成、国データ生成を担当
 */
@Service
public class GeminiService {
    
    // 設定ファイルからGemini APIキーを注入
    @Value("${gemini.api.key}")
    private String apiKey;
    
    // 使用するGeminiモデル名を注入
    @Value("${gemini.model}")
    private String model;
    
    // HTTP通信用クライアント
    private final OkHttpClient httpClient = new OkHttpClient();
    // JSONパース用オブジェクトマッパー
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // デフォルトAPIキー（設定されていない場合の識別用）
    private static final String DEFAULT_API_KEY = "your-actual-gemini-api-key-here";
    
    // API呼び出し失敗時のフォールバック用国データ
    // 配列形式: [英語名, 日本語名, 国旗URL]
    private static final String[][] FALLBACK_COUNTRIES = {
        {"Japan", "日本", "https://flagcdn.com/h240/jp.png"},
        {"United States", "アメリカ合衆国", "https://flagcdn.com/h240/us.png"},
        {"France", "フランス", "https://flagcdn.com/h240/fr.png"},
        {"Germany", "ドイツ", "https://flagcdn.com/h240/de.png"},
        {"Italy", "イタリア", "https://flagcdn.com/h240/it.png"},
        {"United Kingdom", "イギリス", "https://flagcdn.com/h240/gb.png"},
        {"Canada", "カナダ", "https://flagcdn.com/h240/ca.png"},
        {"Australia", "オーストラリア", "https://flagcdn.com/h240/au.png"},
        {"Brazil", "ブラジル", "https://flagcdn.com/h240/br.png"},
        {"China", "中国", "https://flagcdn.com/h240/cn.png"},
        {"South Korea", "韓国", "https://flagcdn.com/h240/kr.png"},
        {"India", "インド", "https://flagcdn.com/h240/in.png"},
        {"Mexico", "メキシコ", "https://flagcdn.com/h240/mx.png"},
        {"Spain", "スペイン", "https://flagcdn.com/h240/es.png"},
        {"Russia", "ロシア", "https://flagcdn.com/h240/ru.png"}
    };
    
    /**
     * ユーザーが入力した質問が適切かどうかを検証
     * @param question 検証する質問文
     * @param country 対象となる国名
     * @return 質問が適切であればtrue、不適切であればfalse
     */
    public boolean validateQuestion(String question, String country) {
        try {
            // Gemini AIに質問の妥当性を確認するプロンプトを作成
            String prompt = String.format("""
                質問: "%s"
                対象国: %s
                
                この質問が以下の条件を満たしているかYes/Noで回答してください：
                1. Yes/No形式で回答できる質問である
                2. 答えに直結しない質問である（例：「この国は○○ですか？」のような直接的な質問ではない）
                
                条件を満たしていればYes、満たしていなければNoで回答してください。
                """, question, country);
            
            // Gemini APIを呼び出して回答を取得
            String response = callGeminiAPI(prompt);
            return response.trim().toLowerCase().startsWith("yes");
            
        } catch (Exception e) {
            // API呼び出し失敗時のエラーハンドリング
            System.err.println("質問検証中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            
            // フォールバック: 単純な文字列比較で国名が含まれていないかチェック
            return !question.toLowerCase().contains(country.toLowerCase());
        }
    }
    
    /**
     * ユーザーの質問に対してAIが回答を生成
     * @param question ユーザーからの質問文
     * @param country 対象となる国名
     * @return AIからの回答（「はい」または「いいえ」）
     */
    public String answerQuestion(String question, String country) {
        try {
            // 質問に対する回答を求めるプロンプトを作成
            String prompt = String.format("""
                質問: "%s"
                対象国: %s
                
                この国について上記の質問に日本語で「はい」または「いいえ」で回答してください。
                回答は必ず「はい」または「いいえ」のみにしてください。
                """, question, country);
            
            // Gemini APIを呼び出して回答を取得
            String response = callGeminiAPI(prompt);
            return response.trim();
            
        } catch (Exception e) {
            // API呼び出し失敗時のエラーハンドリング
            System.err.println("質問回答中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            
            // フォールバック: デフォルトで「いいえ」を返す
            return "いいえ";
        }
    }
    
    /**
     * 指定されたタイプのヒントを生成
     * @param hintType ヒントの種類（主食、面積、言語など）
     * @param country 対象となる国名
     * @return 生成されたヒント文
     */
    public String getHint(String hintType, String country) {
        try {
            // ヒントタイプに応じたプロンプトを生成
            String prompt = getHintPrompt(hintType, country);
            
            // Gemini APIを呼び出してヒントを取得
            return callGeminiAPI(prompt);
            
        } catch (Exception e) {
            // API呼び出し失敗時のエラーハンドリング
            System.err.println("ヒント生成中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            
            // フォールバック: エラーメッセージを返す
            return "ヒント情報を取得できませんでした。";
        }
    }
    
    /**
     * ランダムな国とその国旗URLを生成
     * @return [英語名, 日本語名, 国旗URL]の配列
     */
    public String[] generateRandomCountryAndFlag() {
        try {
            // 国データ生成を求めるプロンプトを作成
            String prompt = """
                世界の国連加盟国から1つの国をランダムに選んで、以下の形式で回答してください：
                
                国名（英語）: [英語の正式名称]
                国名（日本語）: [日本語の国名]
                国旗URL: https://flagcdn.com/h240/[2文字の国コード].png
                
                例：
                国名（英語）: Japan
                国名（日本語）: 日本
                国旗URL: https://flagcdn.com/w640/jp.png
                
                必ずこの形式で回答してください。
                """;
            
            // Gemini APIを呼び出して国データを取得
            String response = callGeminiAPI(prompt);
            return parseCountryResponse(response);
            
        } catch (Exception e) {
            // API呼び出し失敗時のエラーハンドリング
            System.err.println("国データ生成中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            
            // フォールバック: 予め定義された国リストからランダムに選択
            int randomIndex = (int) (Math.random() * FALLBACK_COUNTRIES.length);
            System.out.println("フォールバックを使用: " + FALLBACK_COUNTRIES[randomIndex][1]);
            return FALLBACK_COUNTRIES[randomIndex].clone();
        }
    }
    
    /**
     * ユーザーの回答が正解かどうかを検証
     * @param userAnswer ユーザーからの回答
     * @param correctCountryEnglish 正解の国名（英語）
     * @param correctCountryJapanese 正解の国名（日本語）
     * @return 正解であればtrue、不正解であればfalse
     */
    public boolean validateAnswer(String userAnswer, String correctCountryEnglish, String correctCountryJapanese) {
        try {
            // 回答検証を求めるプロンプトを作成
            String prompt = String.format("""
                ユーザーの回答: "%s"
                正解の国（英語）: %s
                正解の国（日本語）: %s
                
                ユーザーの回答が正解かどうかを判定してください。
                以下の場合は正解とみなしてください：
                - 英語名の完全一致または略称（例：USA、UK）
                - 日本語名の完全一致
                - 一般的な別名（例：アメリカ合衆国→アメリカ）
                
                正解の場合は「正解」、不正解の場合は「不正解」のみ回答してください。
                """, userAnswer, correctCountryEnglish, correctCountryJapanese);
            
            // Gemini APIを呼び出して判定結果を取得
            String response = callGeminiAPI(prompt);
            return response.trim().equals("正解");
            
        } catch (Exception e) {
            // API呼び出し失敗時のエラーハンドリング
            System.err.println("回答検証中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            
            // フォールバック: 基本的な文字列比較を実行
            String lowerAnswer = userAnswer.toLowerCase().trim();
            boolean isCorrect = lowerAnswer.equals(correctCountryEnglish.toLowerCase()) || 
                               lowerAnswer.equals(correctCountryJapanese.toLowerCase());
            
            System.out.println("フォールバック検証を使用: " + (isCorrect ? "正解" : "不正解"));
            return isCorrect;
        }
    }
    
    /**
     * ヒントタイプに応じたプロンプトを生成
     * @param hintType ヒントの種類
     * @param country 対象となる国名
     * @return 生成されたプロンプト文
     */
    private String getHintPrompt(String hintType, String country) {
        return switch (hintType) {
            case "主食" -> String.format(
                "%sの主食について、自然な日本語で短く教えてください。国名や記号は使わず、「〜が主食です」のような形で回答してください。", 
                country);
            case "面積" -> String.format(
                "%sの面積を日本と比較して、自然な日本語で短く教えてください。国名や記号は使わず、「日本の〜倍の面積です」のような形で回答してください。", 
                country);
            case "言語" -> String.format(
                "%sの公用語について、自然な日本語で短く教えてください。国名や記号は使わず、「〜語が公用語です」のような形で回答してください。", 
                country);
            default -> "ヒント情報を提供してください。";
        };
    }
    
    /**
     * Gemini APIを呼び出してプロンプトに対する回答を取得
     * @param prompt APIに送信するプロンプト文
     * @return APIからの回答文
     * @throws Exception API呼び出し失敗時
     */
    private String callGeminiAPI(String prompt) throws Exception {
        // APIキーが設定されていない場合はフォールバック応答を使用
        if (apiKey == null || apiKey.trim().isEmpty() || DEFAULT_API_KEY.equals(apiKey)) {
            System.out.println("Gemini APIキーが設定されていません。フォールバック応答を使用します。");
            return getFallbackResponse(prompt);
        }
        
        // Gemini API のエンドポイントURL を構築
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;
        
        try {
            // APIリクエストボディを構築
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));
            
            // JSONにシリアライズ
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            // HTTPリクエストボディを作成
            RequestBody body = RequestBody.create(
                jsonBody, 
                MediaType.parse("application/json")
            );
            
            // HTTPリクエストを構築
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
            
            // API呼び出しを実行
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("API呼び出しが失敗しました。HTTPコード: " + response.code() + ", メッセージ: " + response.message());
                }
                
                // レスポンスボディを取得
                String responseBody = response.body().string();
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    throw new IOException("APIからの応答が空です");
                }
                
                // JSONレスポンスをパース
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                // 応答からテキスト部分を抽出
                JsonNode candidates = jsonResponse.path("candidates");
                if (candidates.isMissingNode() || candidates.size() == 0) {
                    throw new IOException("APIレスポンスに候補が含まれていません");
                }
                
                JsonNode textNode = candidates.get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text");
                        
                if (textNode.isMissingNode()) {
                    throw new IOException("APIレスポンスにテキストが含まれていません");
                }
                
                return textNode.asText();
            }
            
        } catch (IOException e) {
            throw new Exception("Gemini API呼び出し中にネットワークエラーが発生しました: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Gemini API呼び出し中に予期しないエラーが発生しました: " + e.getMessage(), e);
        }
    }
    
    /**
     * API呼び出し失敗時に使用するフォールバック応答を生成
     * @param prompt 元のプロンプト文
     * @return プロンプトの内容に応じたデフォルト応答
     */
    private String getFallbackResponse(String prompt) {
        // 質問検証の場合
        if (prompt.contains("条件を満たしているか")) {
            return "Yes";
        } 
        // Yes/No質問への回答の場合
        else if (prompt.contains("はい」または「いいえ")) {
            return Math.random() > 0.5 ? "はい" : "いいえ";
        } 
        // 主食ヒントの場合
        else if (prompt.contains("主食")) {
            return "米が主食です。";
        } 
        // 面積ヒントの場合
        else if (prompt.contains("面積")) {
            return "日本の約2倍の面積です。";
        } 
        // 言語ヒントの場合
        else if (prompt.contains("言語")) {
            return "英語が公用語です。";
        }
        // その他の場合のデフォルト応答
        return "情報を取得できませんでした。";
    }
    
    /**
     * Gemini APIからの国データ応答をパースして配列に変換
     * @param response APIからの生の応答文
     * @return [英語名, 日本語名, 国旗URL]の配列
     */
    private String[] parseCountryResponse(String response) {
        try {
            // 改行で分割して各行を処理
            String[] lines = response.split("\n");
            String englishName = null;
            String japaneseName = null;
            String flagUrl = null;
            
            // 各行をチェックして必要な情報を抽出
            for (String line : lines) {
                line = line.trim();
                
                // 英語名を抽出
                if (line.startsWith("国名（英語）:") || line.startsWith("国名(英語):")) {
                    englishName = line.substring(line.indexOf(":") + 1).trim();
                } 
                // 日本語名を抽出
                else if (line.startsWith("国名（日本語）:") || line.startsWith("国名(日本語):")) {
                    japaneseName = line.substring(line.indexOf(":") + 1).trim();
                } 
                // 国旗URLを抽出
                else if (line.startsWith("国旗URL:")) {
                    flagUrl = line.substring(line.indexOf(":") + 1).trim();
                }
            }
            
            // すべての必要な情報が取得できた場合は配列として返す
            if (englishName != null && japaneseName != null && flagUrl != null) {
                System.out.println("国データのパースに成功: " + japaneseName);
                return new String[]{englishName, japaneseName, flagUrl};
            } else {
                System.err.println("国データの一部が不足しています。英語名: " + englishName + ", 日本語名: " + japaneseName + ", URL: " + flagUrl);
            }
            
        } catch (Exception e) {
            System.err.println("国データ応答のパースに失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
        
        // パース失敗時のフォールバック（日本を返す）
        System.out.println("パース失敗のため日本をフォールバックとして使用");
        return new String[]{"Japan", "日本", "https://flagcdn.com/w640/jp.png"};
    }
}