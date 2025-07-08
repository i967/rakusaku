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
import org.springframework.web.bind.annotation.SessionAttributes; // Import this
import org.springframework.web.bind.support.SessionStatus;     // Import this

import com.example.demo.entity.ProductList;
import com.example.demo.entity.ShopOrder;
import com.example.demo.entity.ShopOrder.OrderStatus;
import com.example.demo.repository.ProductListRepository;
import com.example.demo.repository.ShopOrderRepository;
import com.example.demo.service.QrCodeService;

@Controller
@RequestMapping("/purchase")
// 1. Tell Spring that this controller manages a session attribute named "cartItems"
@SessionAttributes("cartItems") 
public class PurchaseController {

    @Autowired
    private ProductListRepository productListRepository;
    
    @Autowired
    private ShopOrderRepository shopOrderRepository;
    
    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam("paymentMethod") String paymentMethod,
                                   @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
                                   Model model,
                                   // 2. Add SessionStatus to the method parameters
                                   SessionStatus sessionStatus) {

        if (selectedGoodsIds == null || selectedGoodsIds.isEmpty()) {
            model.addAttribute("message", "お支払いに必要な情報が不足しています");
            return "payment_result";
        }

        List<ProductList> selectedItems = productListRepository.findAllById(selectedGoodsIds);
        List<String> createdOrderNumbers = new ArrayList<>();

        for (ProductList p : selectedItems) {
            ShopOrder newOrder = new ShopOrder();
            String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            newOrder.setOrderNumber(orderNumber);
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
        
        // 3. Mark the session as complete to clear the cart
        sessionStatus.setComplete();

        return "payment";
    }
}