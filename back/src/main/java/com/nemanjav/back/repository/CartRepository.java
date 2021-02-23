package com.nemanjav.back.repository;

import com.nemanjav.back.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart , Long> {
}
