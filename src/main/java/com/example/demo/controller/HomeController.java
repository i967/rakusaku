package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.StoreRepository;

@Controller
public class HomeController {

    private final StoreRepository storeRepository;

    public HomeController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @GetMapping("/")
    public String root() {
        // "home.html" を表示するように変更
        return "home";
    }

    @GetMapping("/storeselect")
    public String storeSelect(Model model) {
        model.addAttribute("stores", storeRepository.findAll());
        return "select"; // ← 実際に存在する "select.html" を指すように修正
    }

    // 以前の /adminhome や /log へのマッピングは不要なため削除します。
    // 必要であれば、適切なコントローラに再配置してください。
}