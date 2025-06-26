package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 修正箇所 ▼▼▼
import com.example.demo.entity.Goods; // Productエンティティの代わりにGoodsエンティティをインポート
import com.example.demo.repository.GoodsRepository; // ProductRepositoryの代わりにGoodsRepositoryをインポート
// 修正箇所 ▲▲▲

@Controller
public class CartController {

    // 修正箇所 ▼▼▼
    @Autowired
    private GoodsRepository goodsRepository; // ProductRepositoryからGoodsRepositoryに変更
    // 修正箇所 ▲▲▲

    @PostMapping("/cart")
    public String showCart(
        @RequestParam(value = "selectedGoodsIds", required = false) List<Long> selectedGoodsIds,
        @RequestParam(value = "storename", required = false) String storename,
        Model model) {

        if (selectedGoodsIds == null || selectedGoodsIds.isEmpty()) {
            // 商品が選択されていない場合でも、戻るボタンのために店舗名を渡すのが親切かもしれません
            model.addAttribute("storename", storename);
            // カートページではなく、元の選択ページに戻すか、メッセージを表示するかは仕様によります
            // ここでは元のページに戻す例をコメントで示します
            // return "redirect:/nextpage?storename=" + storename; 
            return "cart"; // 現在の実装に合わせてcartを返しますが、中身は空になります
        }

        // 修正箇所 ▼▼▼
        // ProductRepositoryからGoodsRepositoryを使い、Goodsのリストを取得する
        List<Goods> selectedItems = goodsRepository.findAllById(selectedGoodsIds);
        // 修正箇所 ▲▲▲
        
        model.addAttribute("cartItems", selectedItems);
        return "cart";
    }
}