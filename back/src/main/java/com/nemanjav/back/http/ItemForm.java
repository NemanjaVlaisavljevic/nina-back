package com.nemanjav.back.http;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class ItemForm {

    @Min(value = 1)
    private Integer quantity;

    @NotEmpty
    private Long productId;

    @NotEmpty
    private String productSize;
}
