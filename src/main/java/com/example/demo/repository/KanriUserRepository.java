package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.KanriUser;

@Repository
public interface KanriUserRepository extends JpaRepository<KanriUser, Long> {

    // loginId で検索（登録時の重複チェック用）
    Optional<KanriUser> findByLoginId(String loginId);

    // loginId と password で検索（ログイン用）
    Optional<KanriUser> findByNameAndPassword(String loginId, String password);

    // loginId と storeId で検索（登録重複チェック用）
    Optional<KanriUser> findByLoginIdAndStoreId(String loginId, Long storeId);
}