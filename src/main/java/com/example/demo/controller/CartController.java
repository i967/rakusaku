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
public class CartController {

    @Autowired
    private ProductListRepository productListRepository; // ★★★ ProductListRepositoryに変更

    @PostMapping("/cart")
    public String showCart(
        @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
        Model model) {

        if (selectedGoodsIds == null || selectedGoodsIds.isEmpty()) {
            return "cart"; 
        }

        List<ProductList> selectedItems = productListRepository.findAllById(selectedGoodsIds);
        
        model.addAttribute("cartItems", selectedItems);
        return "cart";
    }
}