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
public class NextPageController {

    @Autowired
    private GoodsRepository goodsRepository;

    @GetMapping("/nextpage")
    public String showNextPage(
        @RequestParam(value = "storename", required = false) String storename,
        Model model) {

        List<Goods> goodsList;
        if (storename != null && !storename.isEmpty()) {
            goodsList = goodsRepository.findByStorename(storename);
        } else {
            goodsList = goodsRepository.findAll();
        }
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("storename", storename);
        return "nextpage";
    }
}
