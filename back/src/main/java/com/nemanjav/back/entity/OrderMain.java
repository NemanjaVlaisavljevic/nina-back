package com.nemanjav.back.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@DynamicUpdate
public class OrderMain {

    public OrderMain(User user){
        this.buyerEmail = user.getEmail();
        this.buyerCity = user.getCity();
        this.buyerStreetAndNumber = user.getStreetAndNumber();
        this.buyerName = user.getFirstName();
        this.buyerPhone = user.getPhone();
        this.buyerLastName = user.getLastName();
        this.orderAmount = user.getCart().getProducts()
                .stream()
                .map(productInOrder -> productInOrder.getProductPrice().multiply(new BigDecimal(productInOrder.getCount())))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal(0));
        this.orderStatus = 0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY , mappedBy = "orderMain")
    private Set<ProductInOrder> products = new HashSet<>();

    @NotEmpty
    private String buyerEmail;

    @NotEmpty
    private String buyerName;

    @NotEmpty
    private String buyerLastName;

    @NotEmpty
    private String buyerPhone;

    @NotEmpty
    private String buyerCity;

    @NotEmpty
    private String buyerStreetAndNumber;

    @NotNull
    private BigDecimal orderAmount;

    /**
     * default 0: new order.
     */
    @NotNull
    @ColumnDefault("0")
    private Integer orderStatus;

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
