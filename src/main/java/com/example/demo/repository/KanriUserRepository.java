package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KanriUser;

public interface KanriUserRepository extends JpaRepository<KanriUser, Long> {

    // ★★★ おそらく、このメソッドの定義が抜けています！ ★★★
    Optional<KanriUser> findByLoginIdAndPassword(String loginId, String password);

    /**
     * 登録時に使用するメソッド（これは元のままでOK）
     */
    Optional<KanriUser> findByLoginIdAndStoreId(String loginId, Long storeId);

}