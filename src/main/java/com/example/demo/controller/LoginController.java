package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    // ログインフォーム表示
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // login.htmlを表示
    }

    // ログイン処理
    @PostMapping("/login")
    public String validateUser(
            @RequestParam String name,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.validateUser(name, password);

        if (user != null) {
            session.setAttribute("loggedInUser", user);

            if ("admin".equals(user.getRole())) {
                return "redirect:/admin/home";
            } else {
                return "redirect:/home";
            }
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "batu"; // 失敗時
        }
    }

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("userName", loggedInUser.getName());
        return "home";
    }

}