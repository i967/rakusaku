package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.PurchaseHistory;
import com.example.demo.repository.PurchaseHistoryRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PurchaseHistoryController {

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @GetMapping("/purchase/history")
    public String showPurchaseHistory(HttpServletRequest request, Model model) {
        String userId = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_id".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        List<PurchaseHistory> historyList = userId != null
                ? purchaseHistoryRepository.findByUserIdOrderByPurchaseAtDesc(userId)
                : List.of();

        model.addAttribute("historyList", historyList);
        return "purchaseHistory";  // テンプレート名（purchaseHistory.html）を返す
    }
}
