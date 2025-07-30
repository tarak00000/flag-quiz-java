package com.example.flagquiz.controller;

import com.example.flagquiz.model.GameState;
import com.example.flagquiz.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    private String handleRequest(RedirectAttributes redirectAttributes, RequestHandler handler) {
        try {
            handler.execute();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @FunctionalInterface
    private interface RequestHandler {
        void execute() throws Exception;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        GameState gameState = (GameState) session.getAttribute("gameState");
        if (gameState != null) {
            model.addAttribute("gameState", gameState);
        }
        return "index";
    }

    @PostMapping("/new_game")
    public String newGame(HttpSession session, RedirectAttributes redirectAttributes) {
        return handleRequest(redirectAttributes, () -> {
            gameService.startNewGame(session);
            redirectAttributes.addFlashAttribute("message", "新しいゲームを開始しました！");
        });
    }

    @PostMapping("/ask_question")
    public String askQuestion(@RequestParam String question, HttpSession session, RedirectAttributes redirectAttributes) {
        return handleRequest(redirectAttributes, () -> {
            String answer = gameService.askQuestion(question, session);
            redirectAttributes.addFlashAttribute("answer", answer);
            redirectAttributes.addFlashAttribute("lastQuestion", question);
        });
    }

    @PostMapping("/get_hint")
    public String getHint(@RequestParam String hintType, HttpSession session, RedirectAttributes redirectAttributes) {
        return handleRequest(redirectAttributes, () -> {
            String hint = gameService.getHint(hintType, session);
            redirectAttributes.addFlashAttribute("hint", hint);
            redirectAttributes.addFlashAttribute("hintType", hintType);
        });
    }

    @PostMapping("/submit_answer")
    public String submitAnswer(@RequestParam String answer, HttpSession session, RedirectAttributes redirectAttributes) {
        return handleRequest(redirectAttributes, () -> {
            String result = gameService.submitAnswer(answer, session);
            if (result.equals("正解！")) {
                redirectAttributes.addFlashAttribute("success", result);
            } else {
                redirectAttributes.addFlashAttribute("error", result);
            }
        });
    }
}