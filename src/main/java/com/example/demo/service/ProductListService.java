package com.example.demo.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ProductList;
import com.example.demo.repository.ProductListRepository;

@Service
public class ProductListService implements ProductListServiceInter{

	private static final Logger logger = LoggerFactory.getLogger(ProductListService.class);


    private final ProductListRepository productListRepository;
    
    @Autowired
    public ProductListService(ProductListRepository productRepository) {
        this.productListRepository = productRepository;
    }
    
    @Override
	public List<ProductList> getAllProductList() {
		
		return productListRepository.findAll();
	}

	@Override
	public ProductList getProductListById(Long id) {
		
		return productListRepository.findById(id).orElse(null);
	}
//ここからProductListを合わせていく
	@Override
	public void saveProductList(ProductList productList) {
		   if (productList == null) {
	            logger.warn("保存しようとした商品オブジェクトがnullです。");
	            return;
	        }
	        if (productList.getKanriUserId() == null || productList.getKanriUserId().trim().isEmpty()) {
	            logger.warn("商品のkanriUserIdが設定されていません。商品名: {}", productList.getStoreName());
	        }
	        logger.info("商品 '{}' (ID: {}) を保存/更新します。店舗ID: {}", productList.getStoreName(), productList.getId(),
	                productList.getKanriUserId());
	        productListRepository.save(productList);
	    }
	
	public ProductList saveAndReturnProduct(ProductList productlist) {
        if (productlist == null) {
            logger.warn("保存しようとした商品オブジェクトがnullです。");
            // nullを返すか、例外をスローするかは設計によります。
            // ここでは null を許容しない前提で進めますが、呼び出し元での null チェックが重要になります。
            throw new IllegalArgumentException("保存する商品オブジェクトがnullであってはなりません。");
        }
        if (productlist.getKanriUserId() == null || productlist.getKanriUserId().trim().isEmpty()) {
            logger.warn("商品のkanriUserIdが設定されていません。商品名: {}", productlist.getStoreName());
            // 必要に応じてここで例外をスローするか、デフォルト値を設定する処理も考えられます。
        }
        logger.info("商品 '{}' (ID: {}) を保存し、結果を返します。店舗ID: {}", productlist.getStoreName(), productlist.getId(),
        		productlist.getKanriUserId());
        return productListRepository.save(productlist); // JpaRepositoryのsaveメソッドは保存/更新されたエンティティを返す
    }
	
	public List<ProductList> findAll() {
        return productListRepository.findAll();
    }

    public ProductList findProductById(Long productId) {
        if (productId == null) {
            return null;
        }
        Optional<ProductList> productOpt = productListRepository.findById(productId);
        return productOpt.orElse(null);
    }


	@Override
	public void deleteProductList(Long productid) {
		if (productid == null) {
            logger.warn("削除しようとした商品IDがnullです。");
            return;
        }
        if (productListRepository.existsById(productid)) {
            productListRepository.deleteById(productid);
            logger.info("商品ID {} を削除しました。", productid);
        } else {
            logger.warn("削除しようとした商品ID {} は存在しません。", productid);
        }
        productListRepository.deleteById(productid);
		
	}
	//管理ユーザーID取り出し
    public List<ProductList> findProductsByKanriUserId(String kanriUserId) {
        if (kanriUserId == null || kanriUserId.trim().isEmpty()) {
            logger.warn("findProductsByKanriUserId がnullまたは空のkanriUserIdで呼び出されました。");
            return Collections.emptyList();
        }
        return productListRepository.findByKanriUserId(kanriUserId);
    }
    
    //商品名のみ呼び出し
    public List<String> getGoodsname() {
        return productListRepository.findAll()
                                    .stream()
                                    .map(ProductList::getGoodsname)  // 商品名だけを抽出
                                    .toList();
    }

    //店舗名のみ呼び出し
    public List<String> getStorename() {
        return productListRepository.findAll()
                                    .stream()
                                    .map(ProductList::getStoreName)  // 店舗名だけを抽出
                                    .toList();
    }
    
    //値段のみ呼び出し
    public List<Integer> getPrice() {
        return productListRepository.findAll()
                                    .stream()
                                    .map(ProductList::getGoodsprice)  // 値段だけを抽出
                                    .toList();
    }
}