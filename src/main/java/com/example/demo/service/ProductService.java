package com.example.demo.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 既存のsaveメソッド (戻り値なし)
    public void save(Product product) {
        if (product == null) {
            logger.warn("保存しようとした商品オブジェクトがnullです。");
            return;
        }
        if (product.getKanriUserId() == null || product.getKanriUserId().trim().isEmpty()) {
            logger.warn("商品のkanriUserIdが設定されていません。商品名: {}", product.getName());
        }
        logger.info("商品 '{}' (ID: {}) を保存/更新します。店舗ID: {}", product.getName(), product.getId(),
                product.getKanriUserId());
        productRepository.save(product);
    }

    // --- ▼▼▼ 追加するメソッド ▼▼▼ ---
    /**
     * 商品を保存し、保存されたProductオブジェクト（IDなどが採番された状態）を返します。
     * @param product 保存する商品オブジェクト
     * @return 保存された商品オブジェクト
     */
    public Product saveAndReturnProduct(Product product) {
        if (product == null) {
            logger.warn("保存しようとした商品オブジェクトがnullです。");
            // nullを返すか、例外をスローするかは設計によります。
            // ここでは null を許容しない前提で進めますが、呼び出し元での null チェックが重要になります。
            throw new IllegalArgumentException("保存する商品オブジェクトがnullであってはなりません。");
        }
        if (product.getKanriUserId() == null || product.getKanriUserId().trim().isEmpty()) {
            logger.warn("商品のkanriUserIdが設定されていません。商品名: {}", product.getName());
            // 必要に応じてここで例外をスローするか、デフォルト値を設定する処理も考えられます。
        }
        logger.info("商品 '{}' (ID: {}) を保存し、結果を返します。店舗ID: {}", product.getName(), product.getId(),
                product.getKanriUserId());
        return productRepository.save(product); // JpaRepositoryのsaveメソッドは保存/更新されたエンティティを返す
    }
    // --- ▲▲▲ 追加するメソッド ▲▲▲ ---

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findProductById(Long productId) {
        if (productId == null) {
            return null;
        }
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.orElse(null);
    }

    public void deleteProductById(Long productId) {
        if (productId == null) {
            logger.warn("削除しようとした商品IDがnullです。");
            return;
        }
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            logger.info("商品ID {} を削除しました。", productId);
        } else {
            logger.warn("削除しようとした商品ID {} は存在しません。", productId);
        }
    }

    public List<Product> findProductsByKanriUserId(String kanriUserId) {
        if (kanriUserId == null || kanriUserId.trim().isEmpty()) {
            logger.warn("findProductsByKanriUserId がnullまたは空のkanriUserIdで呼び出されました。");
            return Collections.emptyList();
        }
        return productRepository.findByKanriUserId(kanriUserId);
    }
}