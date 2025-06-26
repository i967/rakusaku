package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.KanriUser;
import com.example.demo.repository.KanriUserRepository;

@Service
public class KanriUserService {

    @Autowired
    private KanriUserRepository kanriUserRepository;

    /**
     * 管理ユーザーを登録（店舗ID付き）
     */
    @Transactional
    public boolean registerKanriUser(KanriUser user) {
        if (user.getStoreId() == null) {
            return false;
        }
        if (kanriUserRepository.findByLoginIdAndStoreId(user.getLoginId(), user.getStoreId()).isPresent()) {
            return false;
        }
        try {
            kanriUserRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ログイン認証（storeId不要）
     */
    public KanriUser validateUser(String loginId, String password) {

        // ▼▼▼▼▼▼▼ ここのメソッド名を修正しました！ ▼▼▼▼▼▼▼
        Optional<KanriUser> userOpt = kanriUserRepository.findByLoginIdAndPassword(loginId, password);
        // ▲▲▲▲▲▲▲ findByNameAndPassword から変更 ▲▲▲▲▲▲▲

        return userOpt.orElse(null);
    }
}