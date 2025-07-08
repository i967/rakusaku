package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.ProductListRepository; // ProductListRepository を使用

@Controller
public class HomeController {

    private final ProductListRepository productListRepository;

    @Autowired
    public HomeController(ProductListRepository productListRepository) {
        this.productListRepository = productListRepository;
    }

    @GetMapping("/")
    public String root() {
        return "home";
    }

    @GetMapping("/storeselect")
    public String storeSelect(Model model) {
        // ProductListテーブルから重複しない店舗名のリストを取得する
        List<String> storeNames = productListRepository.findDistinctStoreNames();
        model.addAttribute("storeNames", storeNames);
        return "select"; 
    }
}