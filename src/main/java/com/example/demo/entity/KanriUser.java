package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "kanriusers")
public class KanriUser {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // ← この行を削除
    private String id; // ← LongからStringに変更

    private String loginId;
    private String password;
    private String name;
    private Long storeId; // storeIdは数値のままなので変更しない

    // ゲッター・セッター
    public String getId() { // ← 戻り値の型をStringに変更
        return id;
    }

    public void setId(String id) { // ← 引数の型をStringに変更
        this.id = id;
    }

    // 他のゲッター・セッターは省略
    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}