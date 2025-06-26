package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
