package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data; // Lombok

@Data // Lombok
@Entity
@Table(name = "shop_orders")
public class ShopOrder {

    public enum OrderStatus {
        PREPARING, // 準備中
        READY_FOR_PICKUP, // 準備完了（受取待ち）
        NOTIFIED_CUSTOMER, // 顧客へ通知済
        COMPLETED //完了・受け渡し済
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber; // 店舗側の注文番号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true) // ★★★ この行を false から true に変更 ★★★
    private Customer customer;

    @Column(name = "item_name", nullable = false)
    private String itemName; // 簡単な商品名

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "store_id", nullable = false)
    private Long storeId;
    @Column(name = "price", nullable = false)
    private Integer price;

// ... ゲッター・セッターも忘れずに追加してください ...
    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}