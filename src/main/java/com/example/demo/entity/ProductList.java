package com.example.demo.entity;
	
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
	
@Entity
@Table(name = "product_list")
public class ProductList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    // ★★★ データベースの`store_name`カラムに正しくマッピング ★★★
    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "goodsname", nullable = false)
    private String goodsname;

    @Column(name = "goodsprice", nullable = false)
    private Integer goodsprice;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
    
    @Column(name = "kanri_user_id", nullable = false)
    private String kanriUserId;

    // --- 以下、ゲッター・セッター ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    // ★★★ storeNameフィールドに対応する正しいゲッター・セッター ★★★
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public Integer getGoodsprice() {
        return goodsprice;
    }

    public void setGoodsprice(Integer goodsprice) {
        this.goodsprice = goodsprice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getKanriUserId() {
        return kanriUserId;
    }

    public void setKanriUserId(String kanriUserId) {
        this.kanriUserId = kanriUserId;
    }
}