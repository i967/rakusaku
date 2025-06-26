package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.KanriUser;
import com.example.demo.entity.PurchaseHistory;
import com.example.demo.repository.PurchaseHistoryRepository;
import com.example.demo.service.KanriUserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class KanriloginController {

    @Autowired
    private KanriUserService kanriUserService;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;
    
    @GetMapping("/kanrilogin")
    public String showLoginForm() {
        return "kanrilogin";
    }
    
    @GetMapping("/somepage")
    public String somePage(HttpSession session, Model model) {
        Long storeId = (Long) session.getAttribute("storeId");
        if (storeId == null) {
            // storeIdがセッションにない＝ログインしていないか、セッションが切れた可能性
            return "redirect:/kanrilogin";
        }
        // storeIdを使った処理
        model.addAttribute("storeId", storeId);

        List<PurchaseHistory> orders = purchaseHistoryRepository.findByStoreId(storeId);
        model.addAttribute("orders", orders);
        
        return "somepage";
    }

    

    @PostMapping("/kanrilogin")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

    	System.out.println("****" + loginId + password);
        KanriUser user = kanriUserService.validateUser(loginId, password);
        if (user != null) {
        	System.out.println(loginId + password);
        	System.out.println(loginId + password);
            session.setAttribute("adminUser", user);
            session.setAttribute("storeId", user.getStoreId()); // KanriUserのstoreIdをセッションに保持
            return "redirect:/kanrihome";
        } else {
            model.addAttribute("error", "ユーザーIDまたはパスワードが正しくありません。");
            return "kanrilogin";
        }
    }
}
