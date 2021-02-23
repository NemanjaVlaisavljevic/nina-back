package com.nemanjav.back.controller;

import com.nemanjav.back.entity.Cart;
import com.nemanjav.back.entity.ProductInOrder;
import com.nemanjav.back.entity.ProductInfo;
import com.nemanjav.back.entity.User;
import com.nemanjav.back.enums.ProductSize;
import com.nemanjav.back.enums.ResultEnum;
import com.nemanjav.back.exception.MyException;
import com.nemanjav.back.http.HttpResponse;
import com.nemanjav.back.http.ItemForm;
import com.nemanjav.back.service.CartService;
import com.nemanjav.back.service.ProductInOrderService;
import com.nemanjav.back.service.ProductInfoService;
import com.nemanjav.back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductInfoService productInfoService;
    private final ProductInOrderService productInOrderService;


    @PostMapping("")
    public ResponseEntity<Cart> mergeCart(@RequestBody Collection<ProductInOrder> products , Principal principal){
        User user = userService.findOne(principal.getName());
        try{
            cartService.mergeLocalCart(products , user);
        }catch (Exception e) {
            ResponseEntity.badRequest().body("Merge Cart Failed");
        }
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @GetMapping("")
    public Cart getCart(Principal principal) {
        User user = userService.findOne(principal.getName());
        return cartService.getCart(user);
    }

    @PostMapping("/add")
    public boolean addToCart(@RequestBody ItemForm itemForm , Principal principal){
        ProductInfo productInfo = productInfoService.findOne(itemForm.getProductId());
        if(productInfo == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if(itemForm.getProductSize() != null){
            productInfo.setProductSize(ProductSize.valueOf(itemForm.getProductSize()));
        }
        try{
           mergeCart(Collections.singleton(new ProductInOrder(productInfo ,itemForm.getQuantity())) , principal);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    @PutMapping("/{productId}")
    public ProductInOrder modifyItemInCart(@PathVariable Long productId  , @RequestBody Integer quantity , Principal principal){
        User user = userService.findOne(principal.getName());
        productInOrderService.update(productId , quantity , user);
        return productInOrderService.findOne(productId , user);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<HttpResponse> deleteItemFromCart(@PathVariable Long productId , Principal principal){
        User user = userService.findOne(principal.getName());
        cartService.delete(productId , user);
        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK.value() ,
                HttpStatus.OK , "Successfully deleted item with id : " + productId , new Date()));
    }

    @DeleteMapping("/{productId}/{productSize}")
    public ResponseEntity<HttpResponse> deleteItemFromCart2(@PathVariable Long productId , @PathVariable String productSize , Principal principal){
        User user = userService.findOne(principal.getName());
        cartService.delete2(productId , productSize , user);
        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK.value() ,
                HttpStatus.OK , "Successfully deleted item with id : " + productId , new Date()));
    }

    @PostMapping("/checkout")
    public ResponseEntity checkout(Principal principal){
        User user = userService.findOne(principal.getName());
        cartService.checkout(user);
        return ResponseEntity.ok("Successful checkout , order object created , cart emptied.");
    }
}
