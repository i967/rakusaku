package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "kanriusers")
public class KanriUser {

    @Id // @GeneratedValue は削除します
    private String id; // ★★★ Long から String に変更 ★★★

    private String loginId;

    private String password;

    private String name;

    private Long storeId;

    // ゲッター・セッターもIDの型に合わせて修正
    public String getId() { // ★★★ 戻り値を String に変更 ★★★
        return id;
    }

    public void setId(String id) { // ★★★ 引数を String に変更 ★★★
        this.id = id;
    }

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