package com.nemanjav.back.dto;

import com.nemanjav.back.entity.ProductIcon;
import com.nemanjav.back.entity.ProductSizeStock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ProductInfoDto {

    private String productName;

    private BigDecimal productPrice;

    private Integer productStock;

    private Set<ProductIcon> productIcons;

    private String productDescription;

    private Set<ProductSizeStock> productSizes;

    private Integer categoryType;

    private Integer productStatus;

    private Integer sold;

}
