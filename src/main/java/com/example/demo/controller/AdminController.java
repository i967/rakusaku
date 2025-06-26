package com.example.demo.controller; // パッケージ名はプロジェクトに合わせてください

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.KanriUser;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @GetMapping("/kanrihome")
    public String showAdminHome(HttpSession session, Model model) { // Modelを追加
        KanriUser user = (KanriUser) session.getAttribute("adminUser");
        if (user == null) {
            return "redirect:/kanrilogin";
        }
        
        // ▼▼▼ 以下を追加 ▼▼▼
        model.addAttribute("adminUserName", user.getName()); // user.getName()をモデルに追加
        // ▲▲▲▲▲▲▲▲▲▲

        return "kanrihome";
    }
    // 以下は各リンク先のコントローラも一緒に用意した例です（任意）

    @GetMapping("/addProduct")
    public String showAddProductPage() {
        return "addProduct"; // → addProduct.html に遷移
    }

    @GetMapping("/viewProfit")
    public String showProfitPage() {
        return "viewProfit"; // → viewProfit.html に遷移
    }
}