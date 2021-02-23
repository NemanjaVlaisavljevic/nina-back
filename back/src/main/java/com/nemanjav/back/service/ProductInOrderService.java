package com.nemanjav.back.service;

import com.nemanjav.back.entity.ProductInOrder;
import com.nemanjav.back.entity.User;
import com.nemanjav.back.repository.ProductInOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ProductInOrderService {

    private final ProductInOrderRepository productInOrderRepository;

    @Transactional
    public void update(Long productInfoId , Integer quantity , User user){
        var op = user.getCart()
                .getProducts()
                .stream()
                .filter(productInOrder -> productInfoId.equals(productInOrder.getProductId()))
                .findFirst();
        op.ifPresent(productInOrder ->{
                productInOrder.setCount(quantity);
                productInOrderRepository.save(productInOrder);
        });
    }

    public ProductInOrder findOne(Long productInfoId , User user){
        var op = user.getCart()
                .getProducts()
                .stream()
                .filter(productInOrder -> productInfoId.equals(productInOrder.getProductId()))
                .findFirst();
        AtomicReference<ProductInOrder> productInOrderAtomicReference = new AtomicReference<>();
        op.ifPresent(productInOrderAtomicReference::set);
        return productInOrderAtomicReference.get();
    }

}
