package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.ProductList;

public interface ProductListRepository extends JpaRepository<ProductList, Long> {

    @Query("SELECT DISTINCT p.storeName FROM ProductList p")
    List<String> findDistinctStoreNames();

    List<ProductList> findByStoreName(String storeName);

    List<ProductList> findByStoreId(Long storeId);
    List<ProductList> findByKanriUserId(String kanriUserId);
}