// com.example.demo.service.LineBotService.java

package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// import com.example.demo.entity.Reservation; // 通知機能では直接使わない可能性
// import com.example.demo.repository.ReservationRepository; // 通知機能では直接使わない可能性
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LineBotService {

    @Value("${line.bot.channel-token}")
    private String channelToken;

    // @Autowired
    // private ReservationRepository reservationRepository; // 予約機能がない場合は不要

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 汎用的な返信メッセージ送信メソッド (既存)
     */
    private void sendReplyMessages(String replyToken, List<Map<String, Object>> messages) {
        // ... (既存のコードはそのまま)
        try {
            String url = "https://api.line.me/v2/bot/message/reply";

            Map<String, Object> body = new HashMap<>();
            body.put("replyToken", replyToken);
            body.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(channelToken);

            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(body),
                    headers
            );

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("LINE返信レスポンス: " + response.getStatusCode() + " - " + response.getBody());
        } catch (JsonProcessingException e) {
            System.err.println("メッセージのJSON変換に失敗しました: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("LINEへのメッセージ送信に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * テキストメッセージを返信 (既存)
     */
    public void replyText(String replyToken, String messageText) {
        // ... (既存のコードはそのまま)
        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");
        message.put("text", messageText);
        sendReplyMessages(replyToken, List.of(message));
    }

    /**
     * クイックリプライ付きのテキストメッセージを返信 (既存)
     */
    public void replyWithQuickReply(String replyToken, String text, List<String> quickReplyOptions) {
        // ... (既存のコードはそのまま)
        List<Map<String, Object>> items = quickReplyOptions.stream()
                .map(option -> {
                    Map<String, Object> action = new HashMap<>();
                    action.put("type", "message");
                    action.put("label", option);
                    action.put("text", option);

                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "action");
                    item.put("action", action);
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> quickReply = new HashMap<>();
        quickReply.put("items", items);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");
        message.put("text", text);
        message.put("quickReply", quickReply);

        sendReplyMessages(replyToken, List.of(message));
    }

    /**
     * ボタンテンプレートメッセージを返信 (既存・アクションタイプを柔軟に使えるように意識)
     * buttonActions の各アクションは { "type": "message", "label": "表示名", "text": "送信テキスト" } や
     * { "type": "postback", "label": "表示名", "data": "action=buy&itemid=123", "displayText": "これを買います" } など
     */
    public void replyWithButtonTemplate(String replyToken, String altText, String title, String text, List<Map<String, Object>> buttonActions) {
        // ... (既存のコードはそのまま)
        Map<String, Object> template = new HashMap<>();
        template.put("type", "buttons");
        if (title != null && !title.isEmpty()) {
            template.put("title", title);
        }
        template.put("text", text);
        template.put("actions", buttonActions);

        Map<String, Object> message = new HashMap<>();
        message.put("type", "template");
        message.put("altText", altText);
        message.put("template", template);

        sendReplyMessages(replyToken, List.of(message));
    }


    /**
     * 【新規追加】プッシュメッセージを送信
     * @param userId 送信先のユーザーID
     * @param messageText 送信するテキストメッセージ
     */
    public void sendPushMessage(String userId, String messageText) {
        try {
            String url = "https://api.line.me/v2/bot/message/push";

            Map<String, Object> textMessage = new HashMap<>();
            textMessage.put("type", "text");
            textMessage.put("text", messageText);

            Map<String, Object> body = new HashMap<>();
            body.put("to", userId); // 送信先ユーザーID
            body.put("messages", List.of(textMessage)); // 送信するメッセージのリスト

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(channelToken); // チャネルアクセストークン

            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(body),
                    headers
            );

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("LINE Push APIレスポンス: " + response.getStatusCode() + " - " + response.getBody());
        } catch (JsonProcessingException e) {
            System.err.println("プッシュメッセージのJSON変換に失敗しました: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("LINEへのプッシュメッセージ送信に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // saveReservationメソッドは予約機能が不要な場合はコメントアウトまたは削除
    /*
    public boolean saveReservation(String userId, String datetime) {
        try {
            Reservation reservation = new Reservation(userId, datetime);
            reservationRepository.save(reservation);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    */
}