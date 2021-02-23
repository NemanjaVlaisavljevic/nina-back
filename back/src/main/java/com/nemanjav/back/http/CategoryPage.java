package com.nemanjav.back.http;

import com.nemanjav.back.entity.ProductInfo;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class CategoryPage {

    private String category;
    private Page<ProductInfo> page;

    public CategoryPage(String category, Page<ProductInfo> page) {
        this.category = category;
        this.page = page;
    }
}
