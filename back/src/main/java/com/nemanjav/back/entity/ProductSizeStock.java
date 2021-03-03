package com.nemanjav.back.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nemanjav.back.enums.ProductSize;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude="productInfo")
public class ProductSizeStock {

    public ProductSizeStock(ProductSize productSize , Integer currentSizeStock , ProductInfo productInfo){
        this.productSize = productSize;
        this.currentSizeStock = currentSizeStock;
        this.productInfo = productInfo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductSize productSize;

    private Integer currentSizeStock;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "product_info_id")
    @JsonIgnore
    private ProductInfo productInfo;

}
