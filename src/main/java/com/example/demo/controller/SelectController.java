package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Goods;
import com.example.demo.repository.GoodsRepository;
@Controller
public class SelectController {

    @Autowired
    private GoodsRepository goodsRepository;

//    @GetMapping("/select")
//    public String showSelectPage(Model model) {
//        // goodsテーブルからdistinctな店舗名だけ取得
//        List<String> storeNames = goodsRepository.findDistinctStoreNames(); //
//        model.addAttribute("stores", storeNames); //
//        return "select"; //
//    }

    @GetMapping("/goods")
    public String showGoodsByStore(@RequestParam("storename") String storename, Model model) {
        List<Goods> goodsList = goodsRepository.findByStorename(storename);
        model.addAttribute("storename", storename);
        model.addAttribute("goodsList", goodsList);
        return "nextpage"; // nextpage.html を表示
    }

}

