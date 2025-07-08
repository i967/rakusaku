package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/userhome")
    public String showUserHomePage() {
        // userhome.html を表示します
        return "userhome";
    }
}