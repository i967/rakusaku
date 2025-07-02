package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.StoreRepository;

@Controller
public class HomeController {

    private final StoreRepository storeRepository;

    public HomeController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @GetMapping("/") // ★★★ ルートURL用のメソッドを追加 ★★★
    public String root() {
        // ルートにアクセスされたら、店舗選択画面にリダイレクトする
        return "redirect:/storeselect";
    }

    @GetMapping("/storeselect")
    public String storeSelect(Model model) {
        model.addAttribute("stores", storeRepository.findAll());
        return "store"; // store.html を返す
    }

    // 以前の /adminhome や /log へのマッピングは不要なため削除します。
    // 必要であれば、適切なコントローラに再配置してください。
}