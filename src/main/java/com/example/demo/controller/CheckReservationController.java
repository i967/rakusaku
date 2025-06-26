package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
// ... 他のimport文 ...

import com.example.demo.entity.KanriUser;
// ★★★ ShopOrderをインポート ★★★
import com.example.demo.entity.ShopOrder;
// ★★★ ShopOrderRepositoryをインポート ★★★
import com.example.demo.repository.ShopOrderRepository;

// PurchaseHistory関連は不要になるので削除
// import com.example.demo.entity.PurchaseHistory;
// import com.example.demo.repository.PurchaseHistoryRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckReservationController {

    // ★★★ リポジトリをShopOrderRepositoryに変更 ★★★
    @Autowired
    private ShopOrderRepository shopOrderRepository; 

    @GetMapping("/check-reservation")
    public String showReservations(Model model, HttpSession session) {
        
        KanriUser kanriUser = (KanriUser) session.getAttribute("adminUser");
        if (kanriUser == null || kanriUser.getStoreId() == null) {
            return "redirect:/kanrilogin";
        }
        Long storeId = kanriUser.getStoreId();

        // ★★★ 取得するデータをShopOrderに変更 ★★★
        List<ShopOrder> orders = shopOrderRepository.findByStoreId(storeId);
        
        // デバッグ用の表示をShopOrderの内容に合わせる
        System.out.println("取得した注文件数: " + orders.size());
        System.out.println("検索対象の店舗ID: " + storeId);
        System.out.println("ログイン中の管理者: " + kanriUser.getName());
        
        // ★★★ モデルに渡す変数名は "orders" のまま。中身がShopOrderのリストになる ★★★
        model.addAttribute("orders", orders);
        model.addAttribute("adminUser", kanriUser);
        return "checkReservation";
    }

    // ★★★ /update-delivered のPOSTメソッドは PurchaseHistory を使う古い機能なので、
    // いったんコメントアウトするか、削除します。後の手順でShopOrder用に作り直します。
    /*
    @PostMapping("/update-delivered")
    @ResponseBody
    public String updateDelivered(@RequestParam("id") Long id) {
        // ... (このメソッドは不要になる) ...
    }
    */
}