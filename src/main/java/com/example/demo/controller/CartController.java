package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.ProductList;
import com.example.demo.repository.ProductListRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CartController {

    @Autowired
    private ProductListRepository goodsRepository;

    @PostMapping("/cart")
    public String showCart(
        @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
        @RequestParam(value = "storename", required = false) String storename,
        HttpServletRequest request,
        Model model) {

        if (selectedGoodsIds == null || selectedGoodsIds.isEmpty()) {
            model.addAttribute("message", "商品が選択されていません。");
            model.addAttribute("storename", storename);
            return "cart";
        }

        List<ProductList> selectedItems = goodsRepository.findAllById(selectedGoodsIds);
        
        // 商品ごとに数量を取得
        List<CartItem> cartItems = new ArrayList<>();
        for (ProductList item : selectedItems) {
            String quantityParam = request.getParameter("quantity_" + item.getId());
            int quantity = 1;
            try {
                quantity = Integer.parseInt(quantityParam);
            } catch (NumberFormatException e) {
                quantity = 1; // 万が一不正な値が来たら1をセット
            }
            cartItems.add(new CartItem(item, quantity));
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("storename", storename);
        return "cart";
    }

    // 商品と数量をまとめた内部クラス
    public static class CartItem {
        private ProductList product;
        private int quantity;

        public CartItem(ProductList product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public ProductList getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}