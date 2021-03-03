package com.nemanjav.back.controller;

import com.nemanjav.back.dto.ProductInfoDto;
import com.nemanjav.back.entity.ProductInfo;
import com.nemanjav.back.http.HttpResponse;
import com.nemanjav.back.service.ProductInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductInfoController {

    private final ProductInfoService productInfoService;


    @GetMapping("/product")
    public Page<ProductInfo> getAllProducts(@RequestParam(value = "page" , defaultValue = "1") Integer page ,
                                            @RequestParam(value = "size" , defaultValue = "5") Integer size){
        PageRequest request = PageRequest.of(page - 1, size);
        return productInfoService.findAllProducts(request);
    }
      // ZA TESTIRANJE(radi)
//    @GetMapping("/product/getSizes/{productId}")
//    public ResponseEntity<Set<ProductSizeStock>> getCurrentProductSizesAndStock(@PathVariable Long productId){
//        ProductInfo existingProductInfo = productInfoService.findOne(productId);
//        if(existingProductInfo != null){
//            return ResponseEntity.ok(existingProductInfo.getProductSizes());
//        }else{
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @GetMapping("/product/{productId}")
    public ProductInfo getOneProduct(@PathVariable Long productId){
        ProductInfo productInfo = productInfoService.findOne(productId);
        return productInfoService.findOne(productId);
    }

    @PostMapping("/admin/product/new")
        public ResponseEntity createNewProduct(@Valid @RequestBody ProductInfoDto productInfoDto , BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body
                    (bindingResult);
        }
        return ResponseEntity.ok(productInfoService.save(productInfoDto));
    }

    @PutMapping("/admin/product/{productId}/edit")
    public ResponseEntity editExistingProduct(@Valid @RequestBody ProductInfoDto productInfoDto
                                              , BindingResult bindingResult , @PathVariable Long productId) throws IOException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult);
        }
        return ResponseEntity.ok(productInfoService.update(productId , productInfoDto));
    }

    @DeleteMapping("/admin/product/{productId}/delete")
    public ResponseEntity<HttpResponse> deleteExistingProduct(@PathVariable Long productId){
        productInfoService.deleteProduct(productId);
        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK.value() , HttpStatus.OK
                , "Succesfully deleted product with id : " + productId , new Date()));
    }

}
