package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.PurchaseHistory;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    
    // ユーザーIDに紐づく購入履歴を取得するメソッド
    List<PurchaseHistory> findByUserId(String userId);
    List<PurchaseHistory> findByUserIdOrderByPurchaseAtDesc(String userId);
    List<PurchaseHistory> findByStoreId(Long storeId);
    List<PurchaseHistory> findByStoreIdAndDelivered(Long storeId, Boolean delivered); 
    
}
