package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table; // ★★★ このimport文を追加 ★★★

@Entity
@Table(name = "products") // ★★★ この一行を追加 ★★★
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer price;
    private Integer stock;
    private String description;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "kanri_user_id")
    private String kanriUserId;

    public String getKanriUserId() {
        return kanriUserId;
    }

    public void setKanriUserId(String kanriUserId) {
        this.kanriUserId = kanriUserId;
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updateDate = LocalDateTime.now();
    }

    // Getter / Setter は変更なし
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}