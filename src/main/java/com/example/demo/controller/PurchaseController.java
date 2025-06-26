package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Goods;
import com.example.demo.entity.PurchaseHistory;
import com.example.demo.entity.ShopOrder;
import com.example.demo.entity.ShopOrder.OrderStatus;
import com.example.demo.repository.GoodsRepository;
import com.example.demo.repository.PurchaseHistoryRepository;
import com.example.demo.repository.ShopOrderRepository;
import com.example.demo.service.QrCodeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;
    @Autowired
    private ShopOrderRepository shopOrderRepository;
    @Autowired
    private QrCodeService qrCodeService;

 // PurchaseController.java

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
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
            List<Goods> selectedItems = goodsRepository.findAllById(selectedGoodsIds);
            List<String> createdOrderNumbers = new ArrayList<>();

            for (Goods g : selectedItems) {
                // --- ShopOrderの作成ロジック（最終整理版） ---
                
                // 1. ShopOrderオブジェクトを生成
                ShopOrder newOrder = new ShopOrder();
                
                // 2. 必須項目をすべて設定
                String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                newOrder.setOrderNumber(orderNumber);
                newOrder.setItemName(g.getGoodsname());
                newOrder.setPrice(g.getGoodsprice());
                newOrder.setStatus(OrderStatus.PREPARING);

                if (g.getStoreId() != null) {
                    newOrder.setStoreId(g.getStoreId().longValue());
                } else {
                    model.addAttribute("message", "商品の店舗情報が不足しており、注文を作成できません。");
                    return "payment_result"; // 失敗画面へ
                }
                
                // 3. 全ての項目を設定した後、一度だけ保存する
                shopOrderRepository.save(newOrder);

                // --- ここまでがShopOrderの修正 ---

                // --- PurchaseHistoryの作成（これは今後、ShopOrderへの一本化が完了すれば不要になります） ---
                PurchaseHistory history = new PurchaseHistory();
                history.setGoodsName(g.getGoodsname());
                history.setGoodsPrice(g.getGoodsprice());
                history.setUserId(userId);
                history.setPurchaseAt(LocalDateTime.now());
                history.setStoreId(g.getStoreId());
                history.setStorename(g.getStorename());
                purchaseHistoryRepository.save(history);
                // --- PurchaseHistoryの作成ここまで ---

                createdOrderNumbers.add(orderNumber);
            }

            // --- QRコード生成と画面表示（ここは変更なし） ---
            if (!createdOrderNumbers.isEmpty()) {
                String combinedOrderNumbers = String.join("\n", createdOrderNumbers);
                String lineUrl = "https://line.me/R/oaMessage/@254gcwky/?" + combinedOrderNumbers;
                String qrCodeBase64 = qrCodeService.generateQrCodeAsBase64(lineUrl);

                model.addAttribute("message", "「" + paymentMethod + "」でのお支払いが完了しました");
                model.addAttribute("qrCode", qrCodeBase64);
                model.addAttribute("orderNumbers", createdOrderNumbers);
            }
            
            return "payment";
        } else {
            model.addAttribute("message", "お支払いに必要な情報が不足しています");
            return "payment_result";
        }
    }}