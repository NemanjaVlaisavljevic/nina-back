package com.nemanjav.back.repository;

import com.nemanjav.back.entity.ProductIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductIconRepository extends JpaRepository<ProductIcon , Long> {

    @Modifying
    @Query(value = "delete from product_icon p where p.product_info_id=:productId" ,nativeQuery = true)
    void deleteProductIcon(@Param("productId") Long productId);
}
