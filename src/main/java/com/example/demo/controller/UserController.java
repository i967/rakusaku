package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @GetMapping("/userhome")
    public String showUserHome(HttpSession session, Model model) {
        Object userObj = session.getAttribute("loggedInUser");

        if (userObj == null) {
            return "redirect:/login";
        }

        // KanriUser と User の可能性があるなら instanceof で判定
        String name = "";
        if (userObj instanceof com.example.demo.entity.User user) {
            name = user.getName();
        } else if (userObj instanceof com.example.demo.entity.KanriUser admin) {
            name = admin.getName();
        }

        model.addAttribute("userName", name);
        return "userhome";
    }
}