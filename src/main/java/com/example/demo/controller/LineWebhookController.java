package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.LineBotService;
import com.example.demo.service.OrderLinkingService;

@RestController
@RequestMapping("/webhook")
public class LineWebhookController {

    @Autowired
    private LineBotService lineBotService;

    @Autowired
    private OrderLinkingService orderLinkingService;

    @PostMapping
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        List<Map<String, Object>> events = (List<Map<String, Object>>) payload.get("events");
        if (events == null) {
            return ResponseEntity.ok("OK");
        }

        for (Map<String, Object> event : events) {
            String eventType = (String) event.get("type");
            Map<String, Object> source = (Map<String, Object>) event.get("source");
            String userId = (String) source.get("userId");

            if (userId == null) continue;

            String replyToken = (String) event.get("replyToken");

            if ("follow".equals(eventType)) {
                orderLinkingService.handleFollowEvent(userId);
                lineBotService.replyText(replyToken, "友だち追加ありがとうございます。購入完了画面のQRコードを読み取り、注文連携を行ってください。");

            } else if ("unfollow".equals(eventType)) {
                System.out.println("User " + userId + " has blocked the bot.");

            } else if ("message".equals(eventType)) {
                Map<String, Object> message = (Map<String, Object>) event.get("message");

                if (!"text".equals(message.get("type"))) {
                    lineBotService.replyText(replyToken, "テキストメッセージのみ認識できます。");
                    continue;
                }

                String userMessageText = ((String) message.get("text")).trim();

                if (userMessageText.startsWith("ORD-")) {
                    // 受信したテキストを改行で分割し、リストに変換
                    // "\\R" は、Windows(CRLF), Mac/Linux(LF)など様々な改行コードに対応します
                    List<String> orderNumbers = Arrays.asList(userMessageText.split("\\R"));
                    
                    // 新しいサービスメソッドを呼び出す
                    orderLinkingService.linkOrdersToCustomer(userId, orderNumbers, replyToken);

                } else {
                    lineBotService.replyText(replyToken, "注文番号を送信すると、LINE連携が行われます。「ORD-」から始まる番号を送信してください。");
                }
            }
        }
        return ResponseEntity.ok("OK");
    }
}