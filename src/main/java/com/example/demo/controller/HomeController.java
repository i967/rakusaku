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

    @GetMapping("/userhome")
    public String home() {
        return "userhome";
    }

    @GetMapping("/storeselect")
    public String storeSelect(Model model) {
        model.addAttribute("stores", storeRepository.findAll());
        return "store"; // store.html を返す
    }

    @GetMapping("/log")
    public String log() {
        return "log";
    }
}