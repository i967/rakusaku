package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.ProductList; // ★★★ ProductListに変更
import com.example.demo.repository.ProductListRepository; // ★★★ ProductListRepositoryに変更

@Controller
public class NextPageController {

    @Autowired
    private ProductListRepository productListRepository; // ★★★ ProductListRepositoryに変更

    @PostMapping("/nextpage")
    public String showNextPage(
            @RequestParam(value = "storename") String storename, // required=falseを削除
            Model model) {

        List<ProductList> productList = productListRepository.findByStoreName(storename); // ★★★ findByStoreNameに変更
        
        model.addAttribute("productList", productList);
        model.addAttribute("selectedStore", storename);
        
        return "nextpage";
    }
}