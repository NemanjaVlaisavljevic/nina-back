package com.nemanjav.back.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude="productInfo")
public class ProductIcon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotBlank
    private String productIcon;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "product_info_id")
    @JsonIgnore
    private ProductInfo productInfo;

}
