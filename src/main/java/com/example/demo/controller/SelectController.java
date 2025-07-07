package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.ProductList;
import com.example.demo.repository.ProductListRepository;
@Controller
public class SelectController {

    @Autowired
    private ProductListRepository productlistRepository;

    @GetMapping("/select")
    public String showSelectPage(Model model) {
        // goodsテーブルからdistinctな店舗名だけ取得
        List<String> storeNames = productlistRepository.findDistinctStoreNames();
        model.addAttribute("stores", storeNames);
        return "select";
    }
    @PostMapping("/nextpage")
    public String showGoodsByStore(@RequestParam("storename") String storename, Model model) {
        // 指定された店舗名で商品を検索
        List<ProductList> goodsList = productlistRepository.findByStoreName(storename);

        model.addAttribute("selectedStore", storename);
        model.addAttribute("goodsList", goodsList);

        return "nextpage"; // nextpage.html を表示
    
    }

}