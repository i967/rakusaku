package com.example.demo.controller;

import java.util.List; // ★★★ Listのimport文を追加 ★★★

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.ProductListRepository; // ★★★ ProductListRepositoryに変更 ★★★

@Controller
public class HomeController {

    // ★★★ ProductListRepositoryを注入 ★★★
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
        // ★★★ 新しく作ったメソッドを呼び出す ★★★
        List<String> storeNames = productListRepository.findDistinctStoreNames();
        
        // ★★★ モデルに "storeNames" という名前で渡す ★★★
        model.addAttribute("storeNames", storeNames);
        return "select"; 
    }
}