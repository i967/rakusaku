package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList; // ★ ArrayListをインポート
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Customer;
import com.example.demo.entity.PurchaseHistory;
import com.example.demo.entity.ShopOrder;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PurchaseHistoryRepository;
import com.example.demo.repository.ShopOrderRepository;

@Service
public class OrderLinkingService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShopOrderRepository shopOrderRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;
    
    @Autowired
    private LineBotService lineBotService; // LineBotServiceも注入

    // ユーザーがLINEでフォローした時の処理
    @Transactional
    public void handleFollowEvent(String userId) {
        // プロフィール情報を取得して顧客として登録または更新
        registerOrUpdateCustomerWithProfile(userId);
    }

    /**
     * 【最終修正版】複数の注文を顧客に紐付け、同時に購入履歴も作成する
     */
    @Transactional
    public void linkOrdersToCustomer(String userId, List<String> orderNumbers, String replyToken) {
        int successCount = 0;
        int alreadyLinkedCount = 0;
        List<String> notFoundNumbers = new ArrayList<>();

        // 顧客情報を最初に一度だけ取得または新規登録
        Customer customer = registerOrUpdateCustomerWithProfile(userId);

        for (String orderNum : orderNumbers) {
            if (orderNum == null || orderNum.trim().isEmpty()) {
                continue;
            }

            Optional<ShopOrder> orderOpt = shopOrderRepository.findByOrderNumber(orderNum.trim());
            if (orderOpt.isPresent()) {
                ShopOrder order = orderOpt.get();
                // 既に別の顧客に紐付いている場合はスキップ
                if (order.getCustomer() != null && !order.getCustomer().getLineUserId().equals(userId)) {
                    notFoundNumbers.add(orderNum.trim() + " (他ユーザーの注文)");
                    continue;
                }
                // 既に自分に紐付いている場合はスキップ
                if (order.getCustomer() != null && order.getCustomer().getLineUserId().equals(userId)) {
                    alreadyLinkedCount++;
                    continue;
                }
                
                // ★★★ ここからが重要な修正 ★★★
                // 1. Customerのヘルパーメソッドを使って、双方向の関連を一度に設定
                customer.addOrder(order);

                // 2. 購入履歴を作成
                createPurchaseHistory(customer, order);
                
                successCount++;

            } else {
                notFoundNumbers.add(orderNum.trim());
            }
        }
        
        // 変更された可能性のある顧客情報を一度だけ保存する
        // (cascade設定により、関連するShopOrderも更新される)
        customerRepository.save(customer);

        // 処理結果をまとめてLINEで返信
        sendLinkingResultReply(replyToken, successCount, alreadyLinkedCount, notFoundNumbers);
    }
    
    /**
     * 購入履歴を作成するプライベートメソッド
     */
    private void createPurchaseHistory(Customer customer, ShopOrder order) {
        PurchaseHistory history = new PurchaseHistory();
        history.setUserId(customer.getLineUserId()); // 顧客のLINEユーザーIDをセット
        history.setGoodsName(order.getItemName());
        history.setGoodsPrice(order.getPrice());
        history.setQuantity(1); // 数量は一旦1で固定
        history.setPurchaseAt(LocalDateTime.now());
        history.setStoreId(order.getStoreId());
        
        // 店舗名はShopOrderにはないので、もし必要なら別途取得するロジックが必要
        // history.setStorename(...); 

        purchaseHistoryRepository.save(history);
    }

    /**
     * 顧客情報を登録または更新するプライベートメソッド
     */
    private Customer registerOrUpdateCustomerWithProfile(String lineUserId) {
        return customerRepository.findByLineUserId(lineUserId)
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setLineUserId(lineUserId);
                    newCustomer.setDisplayName("LINE User"); // 仮の表示名
                    return customerRepository.save(newCustomer);
                });
    }

    /**
     * 連携結果をLINEで返信するプライベートメソッド
     */
    private void sendLinkingResultReply(String replyToken, int success, int already, List<String> notFound) {
        StringBuilder replyMessage = new StringBuilder();
        if (success > 0) {
            replyMessage.append(success).append("件の注文をLINEと連携しました。\n");
        }
        if (already > 0) {
            replyMessage.append(already).append("件は、既に連携済みでした。\n");
        }
        if (!notFound.isEmpty()) {
            replyMessage.append("以下の注文番号は見つかりませんでした：\n").append(String.join(", ", notFound));
        }

        if (replyMessage.length() > 0) {
            lineBotService.replyText(replyToken, replyMessage.toString().trim());
        } else {
            lineBotService.replyText(replyToken, "連携できる有効な注文番号がありませんでした。");
        }
    }
}