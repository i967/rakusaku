package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.KanriUser; // ★★★ KanriUserをインポート ★★★
import com.example.demo.entity.ProductList;
import com.example.demo.repository.ProductListRepository;

import jakarta.servlet.http.HttpSession; // ★★★ HttpSessionをインポート ★★★
import jakarta.validation.Valid;

@Controller
public class ProductController {

    @Autowired
    private ProductListRepository productlistRepository;

    // 新商品登録フォームを表示
    @GetMapping("/admin/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductList());
        return "addProduct";
    }

    // 在庫一覧ページを表示 (ログイン中の管理者の商品のみ表示するように修正)
    @GetMapping("/inventoryList")
    public String showInventoryPage(Model model, HttpSession session) { // HttpSessionを追加
        KanriUser adminUser = (KanriUser) session.getAttribute("adminUser");
        if (adminUser == null) {
            return "redirect:/kanrilogin";
        }
        
        // ログイン中の管理者の店舗IDで商品を絞り込む
        List<ProductList> products = productlistRepository.findByStoreId(adminUser.getStoreId());
        
        model.addAttribute("products", products);
        return "inventoryList";
    }

    /**
     * 【修正版】フォーム送信の処理 (商品登録)
     */
    @PostMapping("/admin/products/add")
    public String addProduct(@Valid @ModelAttribute("product") ProductList product, BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession session) { // ★★★ HttpSession を引数に追加 ★★★

        if (bindingResult.hasErrors()) {
            return "addProduct";
        }
        
        // ★★★ ここから処理を追加 ★★★
        // 1. セッションからログイン中の管理者情報を取得
        KanriUser adminUser = (KanriUser) session.getAttribute("adminUser");
        if (adminUser == null) {
            return "redirect:/kanrilogin";
        }
        
        // 2. 新しい商品情報に、管理者の店舗IDと店舗名を設定
        product.setStoreId(adminUser.getStoreId());
        product.setStoreName(adminUser.getName());
        // ★★★ ここまで処理を追加 ★★★

        productlistRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "商品が正常に登録されました。");
        return "redirect:/inventoryList";
    }

    // 商品の在庫数更新処理 (変更なし)
    @PostMapping("/admin/products/updateStock/{id}")
    public String updateProductStock(@PathVariable("id") Long id,
            @RequestParam("stock") Integer stock,
            RedirectAttributes redirectAttributes) {
        Optional<ProductList> productOptional = productlistRepository.findById(id);
        if (productOptional.isPresent()) {
            ProductList product = productOptional.get();
            if (stock == null || stock < 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "在庫数は0以上の有効な数値である必要があります。");
                return "redirect:/inventoryList";
            }
            product.setStock(stock);
            productlistRepository.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "商品ID: " + id + " の在庫数が更新されました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "商品ID: " + id + " が見つかりませんでした。");
        }
        return "redirect:/inventoryList";
    }

    // 商品削除処理 (変更なし)
    @PostMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<ProductList> productOptional = productlistRepository.findById(id);
        if (productOptional.isPresent()) {
            productlistRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "商品ID: " + id + " が削除されました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "商品ID: " + id + " が見つかりませんでした。");
        }
        return "redirect:/inventoryList";
    }
}