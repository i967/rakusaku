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

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;

import jakarta.validation.Valid;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 新商品登録フォームを表示
    @GetMapping("/admin/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "addProduct";
    }

    @GetMapping("/inventoryList")
    public String showInventoryPage(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "inventoryList";
    }

    // フォーム送信の処理 (商品登録)
    @PostMapping("/admin/products/add")
    public String addProduct(@Valid @ModelAttribute Product product, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // productエンティティの stock フィールドに対するバリデーションエラーがあるか確認
            // 例えば、@Min(0) などを Product エンティティの stock フィールドに付与している場合
            return "addProduct";
        }

        productRepository.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "商品が正常に登録されました。");
        return "redirect:/inventoryList";
    }

    // 商品の在庫数更新処理
    @PostMapping("/admin/products/updateStock/{id}") // パスを updateStock に変更
    public String updateProductStock(@PathVariable("id") Long id,
            @RequestParam("stock") Integer stock, // パラメータ名を "stock" に変更, 型を Integer に
            RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            // 在庫数のバリデーション (例: stock >= 0)
            if (stock == null || stock < 0) { // null チェックも追加
                redirectAttributes.addFlashAttribute("errorMessage", "在庫数は0以上の有効な数値である必要があります。");
                return "redirect:/inventoryList";
            }
            product.setStock(stock); // product.setStock() を使用
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("successMessage", "商品ID: " + id + " の在庫数が更新されました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "商品ID: " + id + " が見つかりませんでした。");
        }
        return "redirect:/inventoryList";
    }

    // 商品削除処理
    @PostMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "商品ID: " + id + " が削除されました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "商品ID: " + id + " が見つかりませんでした。");
        }
        return "redirect:/inventoryList";
    }
}