package com.nemanjav.back.dto;

import com.nemanjav.back.entity.ProductIcon;
import com.nemanjav.back.entity.ProductSizeStock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.SortNatural;

import java.math.BigDecimal;
import java.util.Set;
import java.util.SortedSet;

@Data
@AllArgsConstructor
@Builder
public class ProductInfoDto {

    private String productName;

    private BigDecimal productPrice;

    private Integer productStock;

    @SortNatural
    private SortedSet<ProductIcon> productIcons;

    private String productDescription;

    private Set<ProductSizeStock> productSizes;

    private Integer categoryType;

    private Integer productStatus;

    private Integer sold;

}
