package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KanriUser;

// ★★★ ここの Long を String に変更 ★★★
public interface KanriUserRepository extends JpaRepository<KanriUser, String> {

    Optional<KanriUser> findByLoginIdAndPassword(String loginId, String password);

    Optional<KanriUser> findByLoginIdAndStoreId(String loginId, Long storeId);

}