package com.example.demo.controller;

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
import com.example.demo.entity.ShopOrder;
import com.example.demo.entity.ShopOrder.OrderStatus;
import com.example.demo.repository.GoodsRepository;
import com.example.demo.repository.ShopOrderRepository;
import com.example.demo.service.QrCodeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

// ★★★ PurchaseHistory関連のimportを削除 ★★★
// import com.example.demo.entity.PurchaseHistory;
// import com.example.demo.repository.PurchaseHistoryRepository;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private GoodsRepository goodsRepository;
    
    // ★★★ PurchaseHistoryRepositoryのAutowiredを削除 ★★★
    // @Autowired
    // private PurchaseHistoryRepository purchaseHistoryRepository;
    
    @Autowired
    private ShopOrderRepository shopOrderRepository;
    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
                                   HttpServletRequest request,
                                   Model model) {

        String userId = null; // userIdは将来的には顧客(Customer)情報に紐付けるのが望ましい
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_id".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        if (selectedGoodsIds != null && !selectedGoodsIds.isEmpty()) { // userIdのチェックは必須ではなくなる
            List<Goods> selectedItems = goodsRepository.findAllById(selectedGoodsIds);
            List<String> createdOrderNumbers = new ArrayList<>();

            for (Goods g : selectedItems) {
                // --- ShopOrderの作成ロジックのみに ---
                ShopOrder newOrder = new ShopOrder();
                String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                newOrder.setOrderNumber(orderNumber);
                newOrder.setItemName(g.getGoodsname());
                newOrder.setPrice(g.getGoodsprice());
                newOrder.setStatus(OrderStatus.PREPARING);

                if (g.getStoreId() != null) {
                    newOrder.setStoreId(g.getStoreId().longValue());
                } else {
                    model.addAttribute("message", "商品の店舗情報が不足しており、注文を作成できません。");
                    return "payment_result";
                }
                
                shopOrderRepository.save(newOrder);
                
                // ★★★ PurchaseHistoryを作成する部分を削除 ★★★

                createdOrderNumbers.add(orderNumber);
            }

            // --- QRコード生成と画面表示（変更なし） ---
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
    }
}