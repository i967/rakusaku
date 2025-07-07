package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.ProductList;

public interface ProductListServiceInter {
    List<ProductList> getAllProductList();
    ProductList getProductListById(Long id);
    void saveProductList(ProductList productList);
    void deleteProductList(Long id);
}