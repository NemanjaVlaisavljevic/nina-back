package com.nemanjav.back.repository;

import com.nemanjav.back.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory , Integer> {

    ProductCategory findByCategoryType(Integer categoryType);

    List<ProductCategory> findAllByOrderByCategoryType();

    List<ProductCategory> findByCategoryTypeInOrderByCategoryTypeAsc(List<Integer> categoryTypeList);
}
