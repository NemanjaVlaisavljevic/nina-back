package com.nemanjav.back.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Cart {

    // Constructor
    public Cart(User user){
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JsonIgnore
    private User user;

    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL
    ,orphanRemoval = true, mappedBy = "cart")
    private Set<ProductInOrder> products = new HashSet<>();
}
