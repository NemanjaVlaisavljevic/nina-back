package com.nemanjav.back.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nemanjav.back.enums.ProductSize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
public class ProductInOrder {

    // Constructor
    public ProductInOrder(ProductInfo productInfo , Integer quantity){
        this.productId = productInfo.getProductId();
        this.categoryType = productInfo.getCategoryType();
        this.productDescription = productInfo.getProductDescription();
        this.productIcon = productInfo.getProductIcons()
                .stream()
                .findFirst()
                .map(ProductIcon::getProductIcon)
                .get();
        this.productName = productInfo.getProductName();
        this.productPrice = productInfo.getProductPrice();
        this.productStock = productInfo.getProductStock();
        this.productSize = productInfo.getProductSize();
        this.count = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private OrderMain orderMain;

    @NotNull
    private Long productId;

    @NotEmpty
    private String productName;

    @NotNull
    private String productDescription;

    private String productIcon;

    @NotNull
    private Integer categoryType;

    @NotNull
    private BigDecimal productPrice;

    @Enumerated(EnumType.STRING)
    private ProductSize productSize;

    @Min(0)
    private Integer productStock;

    @Min(1)
    private Integer count;

}
