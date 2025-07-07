package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.KanriUser;

@Repository
// ★★★ ここの Long を String に変更 ★★★
public interface KanriUserRepository extends JpaRepository<KanriUser, String> {

    // loginId で検索
    Optional<KanriUser> findByLoginId(String loginId);

    // loginId と password で検索
    Optional<KanriUser> findByLoginIdAndPassword(String loginId, String password);

    // loginId と storeId で検索
    Optional<KanriUser> findByLoginIdAndStoreId(String loginId, Long storeId);
}