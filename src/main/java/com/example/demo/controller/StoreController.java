package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // username と password を受け取る
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.KanriUser;
import com.example.demo.service.KanriUserService; // KanriUserService を使用

import jakarta.servlet.http.HttpSession;

@Controller
public class StoreController { // クラス名は StoreController のままですが、役割は管理者ユーザー追加です

    @Autowired
    private KanriUserService kanriUserService;

    /**
     * 管理者ユーザー追加フォーム表示 (元 新規店舗追加フォーム)
     *
     * @param session HttpSession
     * @param model Model
     * @return ビュー名 (addStore.html または addKanriUser.html など実態に合わせることを推奨)
     */
    @GetMapping("/admin/kanriusers/add") // URLパスはこのまま使用
    public String showAddUserForm(HttpSession session, Model model) { // メソッド名を実態に合わせて変更推奨 (例: showAddUserForm)
        // (必要であればログインチェックなどをここに実装)
        model.addAttribute("kanriUser", new KanriUser()); // フォームバインディング用オブジェクト
        return "addStore"; // ★ビュー名は "addStore" のまま。実態に合わせて変更推奨 (例: "addKanriUser")
    }

    /**
     * 管理者ユーザー登録処理 (元 新規店舗追加処理)
     *
     * @param name ユーザー名 (リクエストパラメータ "username" から取得)
     * @param password パスワード (リクエストパラメータ "password" から取得)
     * @param session HttpSession
     * @param redirectAttributes RedirectAttributes
     * @param model Model
     * @return リダイレクト先またはビュー名
     */
    @PostMapping("/admin/kanriusers/add") // URLパスはこのまま使用
    public String addUser( // メソッド名を実態に合わせて変更推奨 (例: addKanriUser)
            @RequestParam("username") String name, // ★ "username" パラメータを "name" として受け取る
            @RequestParam("password") String password,
            // @RequestParam("storename") String storename, // ← storename は不要なので削除
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        // (必要であればログインチェックなどをここに実装)

        // 入力値のバリデーション (name と password のみ)
        if (name == null || name.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            model.addAttribute("error", "ユーザー名とパスワードの両方を入力してください。");
            // エラー時にフォームの値を保持 (フォームバッキングオブジェクトを再度設定するとより良い)
            KanriUser formWithErrors = new KanriUser();
            formWithErrors.setName(name); // 入力された名前を保持
            // パスワードはセキュリティ上、エラー時に再表示しない方が良い場合もあります
            model.addAttribute("kanriUser", formWithErrors);
            return "addStore"; // 再度フォーム画面を表示
        }

        // KanriUserService のメソッドを呼び出す (name と password のみ渡す)
        // ★ サービス側の registerKanriUser メソッドも (String name, String password) になっている必要があります
        // 修正前：
        // boolean success = kanriUserService.registerKanriUser(name, password);

        // 修正後：
        // 1. KanriUserオブジェクトを生成
        KanriUser newUser = new KanriUser();
        // 2. 画面から受け取った値をセット
        newUser.setName(name);
        newUser.setPassword(password); // パスワードはサービス側でハッシュ化することを推奨
        // 3. オブジェクトをサービスに渡す
        boolean success = kanriUserService.registerKanriUser(newUser);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "管理者ユーザーが正常に追加されました。");
            return "redirect:/admin/kanriusers/add"; // 成功時は再度追加フォームへリダイレクト (PRGパターン)
        } else {
            model.addAttribute("error", "登録に失敗しました。ユーザー名が既に存在する可能性があります。");
            // エラー時にフォームの値を保持
            KanriUser formWithErrors = new KanriUser();
            formWithErrors.setName(name);
            model.addAttribute("kanriUser", formWithErrors);
            return "addStore"; // 再度フォーム画面を表示
        }
    }
}