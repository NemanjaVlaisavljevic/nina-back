package com.nemanjav.back.controller;

import com.nemanjav.back.entity.OrderMain;
import com.nemanjav.back.service.OrderService;
import com.nemanjav.back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@CrossOrigin
public class OrderMainController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/all")
    public Page<OrderMain> getMyOrders(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                       Authentication authentication){
        PageRequest request = PageRequest.of(page - 1, size);
        Page<OrderMain> orderPage;
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("USER"))){
            orderPage = orderService.findByBuyerEmail(authentication.getName(), request);
        }else{
            orderPage = orderService.findAll(request);
        }
        return orderPage;
    }

    @PatchMapping("/cancel/{orderId}")
    public ResponseEntity<OrderMain> cancelOrderById(@PathVariable Long orderId , Authentication authentication){
        OrderMain orderMain = orderService.findOne(orderId);
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("USER"))
                && !orderMain.getBuyerEmail().equals(authentication.getName())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(orderService.cancel(orderId));
    }

    @PatchMapping("/finish/{orderId}")
    public ResponseEntity<OrderMain> finishOrderById(@PathVariable Long orderId , Authentication authentication){
        OrderMain orderMain = orderService.findOne(orderId);
        if(!orderMain.getBuyerEmail().equals(authentication.getName())
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("USER"))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(orderService.finish(orderId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderMain> getOneOrderById(@PathVariable Long orderId , Authentication authentication){
        boolean isCustomer = authentication.getAuthorities().contains(new SimpleGrantedAuthority("USER"));
        OrderMain orderMain = orderService.findOne(orderId);
        if(isCustomer && !orderMain.getBuyerEmail().equals(authentication.getName())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(orderMain);
    }

}
