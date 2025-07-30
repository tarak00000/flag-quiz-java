package com.example.flagquiz.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");
        String message = (String) request.getAttribute("jakarta.servlet.error.message");
        
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", message);
        if (exception != null) {
            model.addAttribute("exceptionMessage", exception.getMessage());
        }
        
        return "error";
    }
}