package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // ★追加
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Customer;
import com.example.demo.entity.KanriUser;
import com.example.demo.entity.ShopOrder;
import com.example.demo.repository.ShopOrderRepository;
import com.example.demo.service.LineBotService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/shop-admin")
public class ShopOrderNotificationController {

    @Autowired
    private ShopOrderRepository shopOrderRepository;

    @Autowired
    private LineBotService lineBotService;

    /**
     * 【修正済】未対応の注文を、自店舗のものだけ表示する
     */
    @GetMapping("/orders/notify")
    public String listOrdersForNotification(Model model, HttpSession session) {
        KanriUser adminUser = (KanriUser) session.getAttribute("adminUser");
        if (adminUser == null || adminUser.getStoreId() == null) {
            return "redirect:/kanrilogin";
        }
        // ★修正ポイント：ログイン中の管理者のstoreIdを取得
        Long storeId = adminUser.getStoreId();

        List<ShopOrder.OrderStatus> statusesToFetch = Arrays.asList(
            ShopOrder.OrderStatus.PREPARING,
            ShopOrder.OrderStatus.READY_FOR_PICKUP
        );
        
        // ★修正ポイント：店舗IDとステータスで絞り込む
        List<ShopOrder> orders = shopOrderRepository.findByStoreIdAndStatusIn(storeId, statusesToFetch);

        model.addAttribute("orders", orders);
        model.addAttribute("adminUser", adminUser);
        return "order-notification-list";
    }

    /**
     * 【修正済】注文履歴の検索ページで、自店舗のものだけを検索・表示する
     */
    @GetMapping("/reservations")
    public String showReservationCheckPage(
            @RequestParam(name = "query", required = false) String query,
            Model model,
            HttpSession session) {

        KanriUser adminUser = (KanriUser) session.getAttribute("adminUser");
        if (adminUser == null || adminUser.getStoreId() == null) {
            return "redirect:/kanrilogin";
        }
        // ★修正ポイント：ログイン中の管理者のstoreIdを取得
        Long storeId = adminUser.getStoreId();

        List<ShopOrder> reservations;
        if (query != null && !query.trim().isEmpty()) {
            // ★修正ポイント：店舗IDと注文番号で絞り込む
            reservations = shopOrderRepository.findByStoreIdAndOrderNumberContaining(storeId, query.trim());
            model.addAttribute("infoMessage", "検索結果: 「" + query + "」に一致する注文");
        } else {
            // ★修正ポイント：店舗IDで絞り込む
            reservations = shopOrderRepository.findByStoreId(storeId);
        }
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("adminUser", adminUser);
        model.addAttribute("query", query);
        return "checkReservation";
    }

    /**
     * 準備完了通知を送信する
     * （共通処理を呼び出すだけなので、大きな変更はなし）
     */
    @PostMapping("/orders/{orderNumber}/send-notification")
    @Transactional // データの更新を伴うため追加
    public String sendOrderReadyNotification(@PathVariable String orderNumber, RedirectAttributes redirectAttributes, HttpSession session) {
        KanriUser adminUser = (KanriUser) session.getAttribute("adminUser");
        if (adminUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
            return "redirect:/kanrilogin";
        }
        return processSendNotification(orderNumber, adminUser, redirectAttributes, "/shop-admin/orders/notify", "準備ができました");
    }

    /**
     * 受け渡し完了通知を送信する
     * （共通処理を呼び出すだけなので、大きな変更はなし）
     */
    @PostMapping("/reservations/{reservationNumber}/notify-completed")
    @Transactional // データの更新を伴うため追加
    public String sendReservationCompletedNotification(
            @PathVariable String reservationNumber,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        KanriUser adminUser = (KanriUser) session.getAttribute("adminUser");
        if (adminUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
            return "redirect:/kanrilogin";
        }
        // ★修正ポイント：完了通知なので、ステータスをCOMPLETEDに変更する共通処理を呼び出す
        return processSendNotification(reservationNumber, adminUser, redirectAttributes, "/shop-admin/reservations", "受け渡しが完了しました", ShopOrder.OrderStatus.COMPLETED);
    }

    /**
     * LINE通知送信の共通処理 (完了ステータスを引数で受け取れるように修正)
     */
    private String processSendNotification(String orderKeyNumber, KanriUser adminUser, RedirectAttributes redirectAttributes, String redirectUrl, String notificationBaseMessage) {
        // デフォルトの完了後ステータスはNOTIFIED_CUSTOMER
        return processSendNotification(orderKeyNumber, adminUser, redirectAttributes, redirectUrl, notificationBaseMessage, ShopOrder.OrderStatus.NOTIFIED_CUSTOMER);
    }

    /**
     * LINE通知送信の共通処理（本体）
     */
    private String processSendNotification(String orderKeyNumber, KanriUser adminUser, RedirectAttributes redirectAttributes, String redirectUrl, String notificationBaseMessage, ShopOrder.OrderStatus statusAfterNotify) {
        ShopOrder order = shopOrderRepository.findByOrderNumber(orderKeyNumber).orElse(null);

        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "注文/予約が見つかりません: " + orderKeyNumber);
            return "redirect:" + redirectUrl;
        }

        // ★修正ポイント：操作する権限があるか（自店舗の注文か）をチェック
        if (!order.getStoreId().equals(adminUser.getStoreId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "この注文を操作する権限がありません。");
            return "redirect:" + redirectUrl;
        }

        if (order.getStatus() == ShopOrder.OrderStatus.COMPLETED) {
            redirectAttributes.addFlashAttribute("infoMessage", "既に完了済みの注文です: " + orderKeyNumber);
            return "redirect:" + redirectUrl;
        }

        Customer customer = order.getCustomer();
        if (customer == null || customer.getLineUserId() == null || customer.getLineUserId().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "顧客情報またはLINEユーザーIDが見つかりません。注文/予約番号: " + orderKeyNumber);
            return "redirect:" + redirectUrl;
        }

        try {
            // 店舗名は adminUser.getName() を使う
            String shopNamePart = (adminUser.getName() != null && !adminUser.getName().isEmpty())
                                  ? "【" + adminUser.getName() + "より】"
                                  : "";

            String message = String.format("%sご注文の「%s」(No: %s) の%s！ご来店お待ちしております。",
                shopNamePart,
                order.getItemName(),
                order.getOrderNumber(),
                notificationBaseMessage
            );

            lineBotService.sendPushMessage(customer.getLineUserId(), message);

            // ★修正ポイント：引数で受け取ったステータスに更新
            order.setStatus(statusAfterNotify);
            shopOrderRepository.save(order);

            redirectAttributes.addFlashAttribute("successMessage", "顧客に「" + notificationBaseMessage + "」通知を送信しました: " + orderKeyNumber);
        } catch (Exception e) {
            System.err.println("LINE通知送信に失敗しました: " + orderKeyNumber + " - " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "LINE通知送信に失敗しました: " + orderKeyNumber);
        }
        return "redirect:" + redirectUrl;
    }
}