// src/main/java/com/example/demo/repository/ShopOrderRepository.java
package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ShopOrder;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {
    
    Optional<ShopOrder> findByOrderNumber(String orderNumber);
    
    List<ShopOrder> findByStatusIn(List<ShopOrder.OrderStatus> statuses);

    List<ShopOrder> findByOrderNumberContaining(String orderNumber);

    // ▼▼▼ 以下3つのメソッドを追記してください ▼▼▼

    // 店舗IDで絞り込んで、ステータスで検索
    List<ShopOrder> findByStoreIdAndStatusIn(Long storeId, List<ShopOrder.OrderStatus> statuses);

    // 店舗IDで絞り込んで、注文番号（部分一致）で検索
    List<ShopOrder> findByStoreIdAndOrderNumberContaining(Long storeId, String orderNumber);
    
    // 店舗IDで絞り込んで全件検索
    List<ShopOrder> findByStoreId(Long storeId);
}