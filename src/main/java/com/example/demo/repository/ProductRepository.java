package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // --- ▼修正箇所▼ ---
    // メソッド名を Product エンティティのフィールド名 "kanriUserId" に合わせて修正
    List<Product> findByKanriUserId(String kanriUserId);
    // --- ▲修正箇所▲ ---
}