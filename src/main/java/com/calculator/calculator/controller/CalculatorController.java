package com.calculator.calculator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CalculatorController {

    @GetMapping("/")
    public String showCalculator(HttpSession session, Model model) {
        String display = (String) session.getAttribute("display");
        if (display == null) display = "0";

        @SuppressWarnings("unchecked")
        List<String> history = (List<String>) session.getAttribute("history");
        if (history == null) history = new ArrayList<>();

        model.addAttribute("result", display);
        model.addAttribute("history", history);
        return "calculator";
    }

    @PostMapping("/calculate")
    public String calculate(
            @RequestParam(required = false) String num,
            @RequestParam(required = false) String operator,
            HttpSession session,
            Model model) {

        String display = (String) session.getAttribute("display");
        if (display == null) display = "0";

        @SuppressWarnings("unchecked")
        List<String> history = (List<String>) session.getAttribute("history");
        if (history == null) history = new ArrayList<>();

        try {
            if (num != null) {
                if ("0".equals(display) && !".".equals(num)) {
                    display = num;
                } else {
                    display += num;
                }
            }

            if (operator != null) {
                if ("C".equals(operator)) {
                    display = "0";
                    session.removeAttribute("first");
                    session.removeAttribute("op");
                } else if ("⌫".equals(operator)) {
                    display = display.length() > 1 ? display.substring(0, display.length() - 1) : "0";
                } else if ("±".equals(operator)) {
                    if (!"0".equals(display)) {
                        if (display.startsWith("-")) display = display.substring(1);
                        else display = "-" + display;
                    }
                } else if ("=".equals(operator)) {
                    Double first = (Double) session.getAttribute("first");
                    String op = (String) session.getAttribute("op");
                    if (first != null && op != null) {
                        double second = Double.parseDouble(display);
                        double result = switch (op) {
                            case "+" -> first + second;
                            case "-" -> first - second;
                            case "*" -> first * second;
                            case "/" -> second == 0 ? Double.NaN : first / second;
                            default -> second;
                        };
                        String historyEntry = first + " " + op + " " + second + " = " + result;
                        history.add(historyEntry);
                        display = String.valueOf(result);
                        session.removeAttribute("first");
                        session.removeAttribute("op");
                    }
                } else { // +, -, *, /
                    session.setAttribute("first", Double.parseDouble(display));
                    session.setAttribute("op", operator);
                    display = "0";
                }
            }
        } catch (Exception e) {
            display = "Error";
            session.removeAttribute("first");
            session.removeAttribute("op");
        }

        session.setAttribute("display", display);
        session.setAttribute("history", history);

        model.addAttribute("result", display);
        model.addAttribute("history", history);
        return "calculator";
    }
}
