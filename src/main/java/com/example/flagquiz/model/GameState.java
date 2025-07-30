package com.example.flagquiz.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private String currentCountryEnglish;
    private String currentCountryJapanese;
    private String countryFlag;
    private int answersLeft = 2;
    private int questionsLeft = 10;
    private int hintsLeft = 3;
    private List<String> hintsUsed = new ArrayList<>();
    private List<String> gameLog = new ArrayList<>();

    public GameState() {}

    public GameState(String englishName, String japaneseName, String flagUrl) {
        this.currentCountryEnglish = englishName;
        this.currentCountryJapanese = japaneseName;
        this.countryFlag = flagUrl;
        this.answersLeft = 2;
        this.questionsLeft = 10;
        this.hintsLeft = 3;
        this.hintsUsed = new ArrayList<>();
        this.gameLog = new ArrayList<>();
    }

    public String getCurrentCountryEnglish() {
        return currentCountryEnglish;
    }

    public void setCurrentCountryEnglish(String currentCountryEnglish) {
        this.currentCountryEnglish = currentCountryEnglish;
    }

    public String getCurrentCountryJapanese() {
        return currentCountryJapanese;
    }

    public void setCurrentCountryJapanese(String currentCountryJapanese) {
        this.currentCountryJapanese = currentCountryJapanese;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public int getAnswersLeft() {
        return answersLeft;
    }

    public void setAnswersLeft(int answersLeft) {
        this.answersLeft = answersLeft;
    }

    public int getQuestionsLeft() {
        return questionsLeft;
    }

    public void setQuestionsLeft(int questionsLeft) {
        this.questionsLeft = questionsLeft;
    }

    public int getHintsLeft() {
        return hintsLeft;
    }

    public void setHintsLeft(int hintsLeft) {
        this.hintsLeft = hintsLeft;
    }

    public List<String> getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(List<String> hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    public List<String> getGameLog() {
        return gameLog;
    }

    public void setGameLog(List<String> gameLog) {
        this.gameLog = gameLog;
    }
}