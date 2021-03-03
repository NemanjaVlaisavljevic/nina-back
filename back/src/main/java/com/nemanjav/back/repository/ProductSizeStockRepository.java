package com.nemanjav.back.repository;

import com.nemanjav.back.entity.ProductSizeStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductSizeStockRepository extends JpaRepository<ProductSizeStock , Long> {

    @Modifying
    @Query(value = "delete from product_size_stock p where p.product_info_id=:productInfoId" ,nativeQuery = true)
    void deleteProductSize(@Param("productInfoId") Long productInfoId);

    @Query(value = "select * from product_size_stock p where p.product_info_id=:productInfoId AND p.product_size=:productSize" , nativeQuery = true)
    ProductSizeStock findProductSizeStock(@Param("productInfoId") Long productInfoId , @Param("productSize") String productSize);
}
