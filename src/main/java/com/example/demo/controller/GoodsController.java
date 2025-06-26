package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.PurchaseHistory;
import com.example.demo.repository.GoodsRepository;
import com.example.demo.repository.PurchaseHistoryRepository;
// import com.example.demo.service.GoodsService; // GoodsServiceは使わないので不要
import com.example.demo.service.ProductService; // ProductServiceをインポート
// ▲▲▲ 修正箇所 ▲▲▲

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GoodsController {

    // ▼▼▼ 修正箇所 ▼▼▼
    @Autowired
    private ProductService productService; // GoodsServiceの代わりにProductServiceを注入
    // ▲▲▲ 修正箇所 ▲▲▲

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    
    @GetMapping("/goodsList")
    public String getAllGoods(HttpServletRequest request, Model model) {
        // (既存のコードはそのまま)
        model.addAttribute("goodsList", productService.findAll()); 
        String userId = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_id".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        List<PurchaseHistory> historyList = (userId != null)
                ? purchaseHistoryRepository.findByUserIdOrderByPurchaseAtDesc(userId)
                : List.of();

        model.addAttribute("historyList", historyList);

        return "goodsList";
    }

    @GetMapping("/select")
    public String selectStore(Model model) {
        model.addAttribute("stores", goodsRepository.findDistinctStoreNames());
        return "select";
    }

    @PostMapping("/nextpage")
    public String nextPage(@RequestParam String storename, Model model) {
        model.addAttribute("selectedStore", storename);
        model.addAttribute("goodsList", goodsRepository.findByStorename(storename));
        // ↓ 実際のHTMLファイル名（拡張子 .html は除く）に修正する
        return "nextpage"; 
    }
}