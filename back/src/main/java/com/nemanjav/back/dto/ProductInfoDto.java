package com.nemanjav.back.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ProductInfoDto {

    @NotNull
    private String productName;

    @NotNull
    private BigDecimal productPrice;

    @NotNull
    @Min(0)
    private Integer productStock;

    private String productIcon;

    private String productDescription;

    @ColumnDefault("0")
    private Integer categoryType;

    /** 0: on-sale 1: off-sale */
    @ColumnDefault("0")
    private Integer productStatus;

}
