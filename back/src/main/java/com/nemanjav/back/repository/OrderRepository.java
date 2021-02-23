package com.nemanjav.back.repository;

import com.nemanjav.back.entity.OrderMain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderMain, Long> {
    Page<OrderMain> findAllByOrderByOrderStatusAscCreateTimeDesc(Pageable pageable);

    Page<OrderMain> findAllByOrderStatusOrderByCreateTimeDesc(Integer status, Pageable pageable);

    Page<OrderMain> findAllByBuyerEmailOrderByOrderStatusAscCreateTimeDesc(String email, Pageable pageable);

    Page<OrderMain> findAllByBuyerPhoneOrderByOrderStatusAscCreateTimeDesc(String phone, Pageable pageable);

    OrderMain findByOrderId(Long orderId);
}
