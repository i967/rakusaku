package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data; // Lombokを使う場合

@Data // Lombok
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_user_id", unique = true, nullable = false)
    private String lineUserId; // LINEから取得するuserId

    @Column(name = "display_name")
    private String displayName; // LINEの表示名など (任意)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Customerが複数のShopOrderを持つ場合
    @OneToMany(mappedBy = "customer")
    private List<ShopOrder> orders;
}