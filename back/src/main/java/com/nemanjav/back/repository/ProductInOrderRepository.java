package com.nemanjav.back.repository;

import com.nemanjav.back.entity.ProductInOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInOrderRepository extends JpaRepository<ProductInOrder , Long> {
}
