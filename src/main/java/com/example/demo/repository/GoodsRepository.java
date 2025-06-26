package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.Goods;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    @Query("SELECT DISTINCT g.storename FROM Goods g")
    List<String> findDistinctStoreNames();
    List<Goods> findByStorename(String storename);
}
