package com.nemanjav.back.entity;

import com.nemanjav.back.enums.ProductSize;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@EqualsAndHashCode
public class ProductInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotNull
    private String productName;

    @NotNull
    private BigDecimal productPrice;

    @NotNull
    @Min(0)
    private Integer productStock;

    private String productDescription;

    @OrderBy("iconOrder ASC")
    @OneToMany(cascade = CascadeType.REMOVE , fetch = FetchType.LAZY , mappedBy = "productInfo")
    private Set<ProductIcon> productIcons = new HashSet<>();

    /** 0: on-sale 1: off-sale */
    @ColumnDefault("0")
    private Integer productStatus;

    @ColumnDefault("0")
    private Integer categoryType;

    @Enumerated(EnumType.STRING)
    private ProductSize productSize;

    @CreationTimestamp
    private Date createTime;

    @UpdateTimestamp
    private Date updateTime;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY , mappedBy = "productInfo")
    private Set<ProductSizeStock> productSizes = new HashSet<>();

    @ColumnDefault("0")
    private Integer sold;

}
