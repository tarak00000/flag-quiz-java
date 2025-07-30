package com.example.flagquiz.service;

import com.example.flagquiz.model.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * ゲームサービス - 国旗クイズゲームのメインロジックを管理
 * セッション管理、質問処理、回答検証などのゲームフローを制御
 */
@Service
public class GameService {
    
    // Gemini AIサービスを注入（質問検証、回答生成、ヒント生成用）
    @Autowired
    private GeminiService geminiService;

    /**
     * セッションから有効なゲーム状態を取得
     * @param session HTTPセッション
     * @return 有効なゲーム状態オブジェクト
     * @throws RuntimeException ゲームが開始されていない場合
     */
    private GameState getValidGameState(HttpSession session) {
        GameState gameState = (GameState) session.getAttribute("gameState");
        if (gameState == null) {
            throw new RuntimeException("ゲームが開始されていません");
        }
        return gameState;
    }

    /**
     * 新しいゲームを開始
     * @param session HTTPセッション
     * @throws Exception 国データ生成に失敗した場合
     */
    public void startNewGame(HttpSession session) throws Exception {
        try {
            // Gemini AIから新しい国データを取得
            String[] countryData = geminiService.generateRandomCountryAndFlag();
            
            // 新しいゲーム状態を作成
            GameState gameState = new GameState(countryData[0], countryData[1], countryData[2]);
            
            // セッションに保存
            session.setAttribute("gameState", gameState);
            
            System.out.println("新しいゲームが開始されました: " + countryData[1]);
            
        } catch (Exception e) {
            System.err.println("ゲーム開始中にエラーが発生しました: " + e.getMessage());
            throw new Exception("ゲームの開始に失敗しました: " + e.getMessage(), e);
        }
    }

    public String askQuestion(String questionText, HttpSession session) throws Exception {
        GameState gameState = getValidGameState(session);
        
        if (gameState.getQuestionsLeft() <= 0) {
            throw new RuntimeException("質問回数が残っていません");
        }
        
        if (questionText == null || questionText.trim().isEmpty()) {
            throw new RuntimeException("質問を入力してください");
        }
        
        boolean isValid = geminiService.validateQuestion(questionText, gameState.getCurrentCountryEnglish());
        if (!isValid) {
            throw new RuntimeException("質問は Yes/No で回答できる形式で、答えに直結しない内容にしてください。");
        }
        
        String answer = geminiService.answerQuestion(questionText, gameState.getCurrentCountryEnglish());
        
        String logEntry = "Q: " + questionText + " → A: " + answer;
        gameState.getGameLog().add(logEntry);
        gameState.setQuestionsLeft(gameState.getQuestionsLeft() - 1);
        
        session.setAttribute("gameState", gameState);
        return answer;
    }

    public String getHint(String hintType, HttpSession session) throws Exception {
        GameState gameState = getValidGameState(session);
        
        if (gameState.getHintsUsed().contains(hintType)) {
            throw new RuntimeException("このヒントは既に使用されています");
        }
        
        if (gameState.getHintsLeft() <= 0) {
            throw new RuntimeException("ヒントは3回まで使用できます");
        }
        
        String hint = geminiService.getHint(hintType, gameState.getCurrentCountryEnglish());
        
        gameState.getHintsUsed().add(hintType);
        gameState.setHintsLeft(gameState.getHintsLeft() - 1);
        session.setAttribute("gameState", gameState);
        
        return hint;
    }

    public String submitAnswer(String answer, HttpSession session) {
        GameState gameState = getValidGameState(session);
        
        if (gameState.getAnswersLeft() <= 0) {
            throw new RuntimeException("回答回数が残っていません");
        }
        
        if (answer == null || answer.trim().isEmpty()) {
            throw new RuntimeException("回答を入力してください");
        }
        
        boolean isCorrect = geminiService.validateAnswer(
            answer.trim(), 
            gameState.getCurrentCountryEnglish(), 
            gameState.getCurrentCountryJapanese()
        );
        
        if (isCorrect) {
            return "正解！";
        } else {
            // 不正解の場合、回答回数を減らす
            gameState.setAnswersLeft(gameState.getAnswersLeft() - 1);
            session.setAttribute("gameState", gameState);
            
            if (gameState.getAnswersLeft() > 0) {
                return "不正解です。残り" + gameState.getAnswersLeft() + "回回答できます。";
            } else {
                return "残念！正解は「" + gameState.getCurrentCountryJapanese() + "」でした。ゲーム終了です。";
            }
        }
    }
}