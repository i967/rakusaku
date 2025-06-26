// GoodsService.java
package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Goods;

public interface GoodsService {
    List<Goods> getAllGoods();
    Goods getGoodsById(Long id);
    void saveGoods(Goods goods);
    void deleteGoods(Long id);
}
