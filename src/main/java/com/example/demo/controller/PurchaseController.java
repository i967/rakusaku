package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.ProductList;
import com.example.demo.entity.PurchaseHistory;
import com.example.demo.repository.ProductListRepository;
import com.example.demo.repository.PurchaseHistoryRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private ProductListRepository productListRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam("paymentMethod") String paymentMethod,
                                 @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
                                 @RequestParam(value = "quantities", required = false) List<Integer> quantities,
                                 HttpServletRequest request,
                                 Model model) {

        String userId = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_id".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        if (selectedGoodsIds != null && !selectedGoodsIds.isEmpty() && userId != null) {

            List<ProductList> selectedItems = productListRepository.findAllById(selectedGoodsIds);

            if (quantities == null || quantities.size() != selectedGoodsIds.size()) {
                model.addAttribute("message", "数量情報が正しくありません");
                return "payment_result";
            }

            int totalPrice = 0;

            for (int i = 0; i < selectedItems.size(); i++) {
                ProductList g = selectedItems.get(i);
                int quantity = quantities.get(i);

                PurchaseHistory history = new PurchaseHistory();
                history.setGoodsName(g.getGoodsname());
                history.setGoodsPrice(g.getGoodsprice());
                history.setUserId(userId);
                history.setPurchaseAt(LocalDateTime.now());
                history.setStoreId(g.getStoreId());
                history.setStorename(g.getStoreName());
                history.setQuantity(quantity);

                purchaseHistoryRepository.save(history);

                totalPrice += g.getGoodsprice() * quantity;
            }

            model.addAttribute("message", "「" + paymentMethod + "」でのお支払いが完了しました");
            model.addAttribute("totalPrice", totalPrice);

            return "payment_result";  // 統一してこの画面だけ使う
        } else {
            model.addAttribute("message", "お支払いに必要な情報が不足しています");
            return "payment_result";
        }
    }
}