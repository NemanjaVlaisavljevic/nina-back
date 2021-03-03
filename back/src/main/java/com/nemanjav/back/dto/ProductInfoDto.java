package com.nemanjav.back.dto;

import com.nemanjav.back.entity.ProductIcon;
import com.nemanjav.back.entity.ProductSizeStock;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ProductInfoDto {

    @NotNull
    private String productName;

    @NotNull
    private BigDecimal productPrice;

    //@NotNull
    //@Min(0)
    private Integer productStock;

    private Set<ProductIcon> productIcons;

    private String productDescription;

    private Set<ProductSizeStock> productSizes;

    @ColumnDefault("0")
    private Integer categoryType;

    /** 0: on-sale 1: off-sale */
    @ColumnDefault("0")
    private Integer productStatus;

}
