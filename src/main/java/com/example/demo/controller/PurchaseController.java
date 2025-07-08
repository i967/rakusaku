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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.demo.entity.ProductList; // ProductListを使用
import com.example.demo.entity.ShopOrder;
import com.example.demo.entity.ShopOrder.OrderStatus;
import com.example.demo.repository.ProductListRepository; // ProductListRepositoryを使用
import com.example.demo.repository.ShopOrderRepository;
import com.example.demo.service.QrCodeService;

@Controller
@RequestMapping("/purchase")
@SessionAttributes("cartItems") // セッション管理の対象を指定
public class PurchaseController {

    @Autowired
    private ProductListRepository productListRepository; // ProductListRepositoryを注入
    
    @Autowired
    private ShopOrderRepository shopOrderRepository;
    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
                                   Model model,
                                   SessionStatus sessionStatus) {

        if (selectedGoodsIds == null || selectedGoodsIds.isEmpty()) {
            model.addAttribute("message", "お支払いに必要な情報が不足しています");
            return "payment_result";
        }

        List<ProductList> selectedItems = productListRepository.findAllById(selectedGoodsIds);
        List<String> createdOrderNumbers = new ArrayList<>();

        for (ProductList p : selectedItems) { // ProductListでループ
            ShopOrder newOrder = new ShopOrder();
            String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            newOrder.setOrderNumber(orderNumber);
            // ProductListのプロパティ(goodsname, goodsprice, storeId)を使う
            newOrder.setItemName(p.getGoodsname()); 
            newOrder.setPrice(p.getGoodsprice());
            newOrder.setStatus(OrderStatus.PREPARING);
            newOrder.setStoreId(p.getStoreId());
            
            shopOrderRepository.save(newOrder);
            createdOrderNumbers.add(orderNumber);
        }

        if (!createdOrderNumbers.isEmpty()) {
            String combinedOrderNumbers = String.join("\n", createdOrderNumbers);
            String lineUrl = "https://line.me/R/oaMessage/@254gcwky/?" + combinedOrderNumbers;
            String qrCodeBase64 = qrCodeService.generateQrCodeAsBase64(lineUrl);
            model.addAttribute("message", "「" + paymentMethod + "」でのお支払いが完了しました");
            model.addAttribute("qrCode", qrCodeBase64);
            model.addAttribute("orderNumbers", createdOrderNumbers);
        }
        
        sessionStatus.setComplete(); // セッションをクリア
        return "payment";
    }
}