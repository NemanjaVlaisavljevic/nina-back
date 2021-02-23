package com.nemanjav.back.repository;

import com.nemanjav.back.entity.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInfoRepository extends JpaRepository<ProductInfo , Long> {
    ProductInfo findByProductId(Long productId);

    Page<ProductInfo> findAllByProductStatusOrderByProductIdAsc(Integer code, Pageable pageable);

    Page<ProductInfo> findAllByOrderByProductId(Pageable pageable);

    Page<ProductInfo> findAllByCategoryTypeOrderByProductIdAsc(Integer categoryType, Pageable pageable);
}
