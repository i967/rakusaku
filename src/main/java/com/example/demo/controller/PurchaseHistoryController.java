package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.ProductList; // ★★★ ProductListをインポート
import com.example.demo.entity.PurchaseHistory;
import com.example.demo.repository.ProductListRepository; // ★★★ ProductListRepositoryをインポート
import com.example.demo.repository.PurchaseHistoryRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PurchaseHistoryController {

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private ProductListRepository productListRepository; // ★★★ ProductListRepositoryを注入

    // ★★★ このメソッドを新たに追加、または修正します ★★★
    @GetMapping("/goodsList")
    public String showGoodsAndHistory(HttpServletRequest request, Model model) {
        
        // 全商品リストを取得
        List<ProductList> productList = productListRepository.findAll();
        model.addAttribute("goodsList", productList); // "goodsList"という名前でモデルに追加

        // --- 以下は購入履歴を取得する既存のロジック ---
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

        return "goodsList"; // goodsList.html を表示
    }

    // 既存の /purchase/history メソッドは、役割が重複するため、
    // 不要であれば削除しても構いません。
    /*
    @GetMapping("/purchase/history")
    public String showPurchaseHistory(HttpServletRequest request, Model model) {
        // ... (このメソッドは上の showGoodsAndHistory と機能が似ている)
    }
    */
}