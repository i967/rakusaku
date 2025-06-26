package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Customer;
import com.example.demo.entity.ShopOrder;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ShopOrderRepository;

@Service
public class OrderLinkingService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShopOrderRepository shopOrderRepository;

    @Autowired
    private LineBotService lineBotService;

    @Transactional
    public void handleFollowEvent(String userId) {
        registerOrUpdateCustomerWithProfile(userId);
    }

    /**
     * 複数注文対応版の連携メソッド
     * @param userId LINEユーザーID
     * @param orderNumbers 注文番号のリスト
     * @param replyToken 返信用のトークン
     */
    @Transactional
    public void linkOrdersToCustomer(String userId, List<String> orderNumbers, String replyToken) {
        int successCount = 0;
        int alreadyLinkedCount = 0;
        List<String> notFoundNumbers = new ArrayList<>();

        // 顧客情報は最初に一度だけ取得・登録
        Customer customer = registerOrUpdateCustomerWithProfile(userId);

        for (String orderNum : orderNumbers) {
            if (orderNum == null || orderNum.trim().isEmpty()) {
                continue;
            }

            Optional<ShopOrder> orderOpt = shopOrderRepository.findByOrderNumber(orderNum.trim());
            if (orderOpt.isPresent()) {
                ShopOrder order = orderOpt.get();
                if (order.getCustomer() != null && order.getCustomer().getLineUserId() != null) {
                    alreadyLinkedCount++;
                } else {
                    order.setCustomer(customer);
                    shopOrderRepository.save(order);
                    successCount++;
                }
            } else {
                notFoundNumbers.add(orderNum.trim());
            }
        }

        // 全ての処理が終わった後に、一度だけまとめて返信する
        StringBuilder replyMessage = new StringBuilder();
        if (successCount > 0) {
            replyMessage.append(successCount).append("件の注文をLINEと連携しました。\n");
        }
        if (alreadyLinkedCount > 0) {
            replyMessage.append(alreadyLinkedCount).append("件は、既に連携済みでした。\n");
        }
        if (!notFoundNumbers.isEmpty()) {
            replyMessage.append("以下の注文番号は見つかりませんでした：\n").append(String.join(", ", notFoundNumbers));
        }

        if (replyMessage.length() > 0) {
            lineBotService.replyText(replyToken, replyMessage.toString().trim());
        } else {
            // 全ての注文番号が空だった場合など
            lineBotService.replyText(replyToken, "連携できる有効な注文番号がありませんでした。");
        }
    }

    private Customer registerOrUpdateCustomerWithProfile(String lineUserId) {
        // (このメソッドは元のままでOKですが、念のため記載)
        // 実際のコードではfetchDisplayNameFromLineAPIを呼び出す必要がありますが、
        // 簡略化のため、ここでは省略します。ご自身の既存コードを使用してください。
        // ※ fetchDisplayNameFromLineAPI を呼び出すコードがこのクラスになければ、
        // 以前のLineWebhookControllerからこのクラスに移動する必要があります。
        
        Optional<Customer> customerOpt = customerRepository.findByLineUserId(lineUserId);

        Customer customer = customerOpt.orElseGet(Customer::new);
        customer.setLineUserId(lineUserId);
        
        // 仮の表示名。本来はAPIで取得
        if(customer.getDisplayName() == null || customer.getDisplayName().isEmpty()){
             customer.setDisplayName("LINE User");
        }

        return customerRepository.save(customer);
    }
}