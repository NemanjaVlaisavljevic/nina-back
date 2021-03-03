package com.nemanjav.back.service;

import com.nemanjav.back.dto.ProductInfoDto;
import com.nemanjav.back.entity.ProductInfo;
import com.nemanjav.back.entity.ProductSizeStock;
import com.nemanjav.back.enums.ProductSize;
import com.nemanjav.back.enums.ProductStatusEnum;
import com.nemanjav.back.enums.ResultEnum;
import com.nemanjav.back.exception.MyException;
import com.nemanjav.back.repository.ProductInfoRepository;
import com.nemanjav.back.repository.ProductSizeStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final ProductInfoRepository productInfoRepository;

    private final CategoryService categoryService;

    private final ProductSizeStockRepository productSizeStockRepository;

    public ProductInfo findOne(Long productId) {
        ProductInfo existingProduct = productInfoRepository.findByProductId(productId);
        if (existingProduct == null) {
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        } else {
            return existingProduct;
        }
    }

    public Page<ProductInfo> findAllUpProducts(Pageable pageable) {
        return productInfoRepository.findAllByProductStatusOrderByProductIdAsc(ProductStatusEnum.UP.getCode(), pageable);
    }

    public Page<ProductInfo> findAllProducts(Pageable pageable) {
        return productInfoRepository.findAllByOrderByProductId(pageable);
    }

    public Page<ProductInfo> findAllInCategory(Integer categoryType, Pageable pageable) {
        return productInfoRepository.findAllByCategoryTypeOrderByProductIdAsc(categoryType, pageable);
    }

    @Transactional
    public void increaseStock(Long productId, int amount) {
        ProductInfo productInfo = findOne(productId);
        if (productInfo == null) throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);

        int update = productInfo.getProductStock() + amount;
        productInfo.setProductStock(update);
        productInfoRepository.saveAndFlush(productInfo);
    }

    @Transactional
    public void increaseStockForClothes(Long productId , int amount , ProductSize productSize){
        ProductInfo productInfo = findOne(productId);
        if(productInfo == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        ProductSizeStock existingProductSizeStock = productSizeStockRepository.findProductSizeStock(productId , productSize.toString());
        int update = existingProductSizeStock.getCurrentSizeStock() + amount;
        int productInfoStock = 0;
        existingProductSizeStock.setCurrentSizeStock(update);
        existingProductSizeStock.setCurrentSizeStock(update);
        for(ProductSizeStock productSizeStock : productInfo.getProductSizes()){
            productInfoStock += productSizeStock.getCurrentSizeStock();
        }
        productInfo.setProductStock(productInfoStock);
        productSizeStockRepository.save(existingProductSizeStock);
        productInfoRepository.save(productInfo);
    }

    @Transactional
    public void decreaseStockForClothes(Long productId , int amount , ProductSize productSize){
        ProductInfo productInfo = findOne(productId);
        if(productInfo == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        ProductSizeStock existingProductSizeStock = productSizeStockRepository.findProductSizeStock(productId , productSize.toString());
        int update = existingProductSizeStock.getCurrentSizeStock() - amount;
        int productInfoStock = 0;
        if (update < 0) {
            throw new MyException(ResultEnum.PRODUCT_NOT_ENOUGH);
        }
        existingProductSizeStock.setCurrentSizeStock(update);
        for(ProductSizeStock productSizeStock : productInfo.getProductSizes()){
            productInfoStock += productSizeStock.getCurrentSizeStock();
        }
        productInfo.setProductStock(productInfoStock);
        productSizeStockRepository.save(existingProductSizeStock);
        productInfoRepository.save(productInfo);
    }

    @Transactional
    public void decreaseStock(Long productId, int amount) {
        ProductInfo productInfo = findOne(productId);
        if (productInfo == null) throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);

        int update = productInfo.getProductStock() - amount;
        if (update < 0){
            throw new MyException(ResultEnum.PRODUCT_NOT_ENOUGH);
        }

        productInfo.setProductStock(update);
        productInfoRepository.saveAndFlush(productInfo);
    }

    @Transactional
    public ProductInfo onSale(Long productId) {
        ProductInfo existingProduct = findOne(productId);
        if (existingProduct == null) {
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (existingProduct.getProductStatus().equals(ProductStatusEnum.UP.getCode())) {
            throw new MyException(ResultEnum.PRODUCT_STATUS_ERROR);
        }
        existingProduct.setProductStatus(ProductStatusEnum.UP.getCode());
        return productInfoRepository.saveAndFlush(existingProduct);
    }

    @Transactional
    public ProductInfo offSale(Long productId) {
        ProductInfo existingProduct = findOne(productId);
        if (existingProduct == null) throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);

        if (existingProduct.getProductStatus().equals(ProductStatusEnum.DOWN.getCode())) {
            throw new MyException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        existingProduct.setProductStatus(ProductStatusEnum.DOWN.getCode());
        return productInfoRepository.save(existingProduct);
    }


    @Transactional
    public ProductInfo update(Long productId, ProductInfoDto productInfoDto) throws IOException {
        // if null throw exception
        ProductInfo productInfo = findOne(productId);
        int productStockSum = 0;
        if (productInfo == null) {
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if (productInfo.getProductStatus() > 1) {
            throw new MyException(ResultEnum.PRODUCT_STATUS_ERROR);
        }
        if (productInfo.getCategoryType() == 0) {
            if(!productInfo.getProductSizes().isEmpty()) {
                for (ProductSizeStock productSizeStock : productInfo.getProductSizes()) {
                    productSizeStockRepository.deleteProductSize(productInfo.getProductId());
                }
            }
            if (productInfoDto.getProductIcon() != null) {
                productInfo.setProductIcon(productInfoDto.getProductIcon());
            }
            if (productInfoDto.getCategoryType() != null) {
                productInfo.setCategoryType(productInfoDto.getCategoryType());
            }
            if (productInfoDto.getProductDescription() != null) {
                productInfo.setProductDescription(productInfoDto.getProductDescription());
            }
            if (productInfoDto.getProductName() != null) {
                productInfo.setProductName(productInfoDto.getProductName());
            }
            if (productInfoDto.getProductPrice() != null) {
                productInfo.setProductPrice(productInfoDto.getProductPrice());
            }
            if (productInfoDto.getProductStatus() != null) {
                productInfo.setProductStatus(productInfoDto.getProductStatus());
            }
            if (!productInfoDto.getProductSizes().isEmpty()) {
                for (ProductSizeStock productSizeStock : productInfoDto.getProductSizes()) {
                    productSizeStock.setProductInfo(productInfo);
                    productInfo.getProductSizes().add(productSizeStock);
                    productStockSum += productSizeStock.getCurrentSizeStock();
                    productSizeStockRepository.save(productSizeStock);
                }
                productInfo.setProductStock(productStockSum);
            }
        } else {
            if (productInfoDto.getProductIcon() != null) {
                productInfo.setProductIcon(productInfoDto.getProductIcon());
            }
            if (productInfoDto.getProductStock() != null) {
                productInfo.setProductStock(productInfoDto.getProductStock());
            }
            if (productInfoDto.getCategoryType() != null) {
                productInfo.setCategoryType(productInfoDto.getCategoryType());
            }
            if (productInfoDto.getProductDescription() != null) {
                productInfo.setProductDescription(productInfoDto.getProductDescription());
            }
            if (productInfoDto.getProductName() != null) {
                productInfo.setProductName(productInfoDto.getProductName());
            }
            if (productInfoDto.getProductPrice() != null) {
                productInfo.setProductPrice(productInfoDto.getProductPrice());
            }
            if (productInfoDto.getProductStatus() != null) {
                productInfo.setProductStatus(productInfoDto.getProductStatus());
            }
        }

        return productInfoRepository.saveAndFlush(productInfo);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        ProductInfo productInfo = findOne(productId);
        if (productInfo == null) {
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        } else {
            productInfoRepository.delete(productInfo);
        }
    }

    @Transactional
    public ProductInfo save(ProductInfoDto productInfoDto) {

        ProductInfo product = new ProductInfo();

        if (productInfoDto.getCategoryType() == 0) {
            Integer productStockSum = 0;
            product.setProductStatus(productInfoDto.getProductStatus());
            product.setProductIcon(productInfoDto.getProductIcon());
            product.setCategoryType(productInfoDto.getCategoryType());
            product.setProductStock(productInfoDto.getProductStock());
            product.setProductDescription(productInfoDto.getProductDescription());
            product.setProductPrice(productInfoDto.getProductPrice());
            product.setProductName(productInfoDto.getProductName());
            if (!productInfoDto.getProductSizes().isEmpty()) {
                for (ProductSizeStock productSizeStock : productInfoDto.getProductSizes()) {
                    productSizeStock.setProductInfo(product);
                    product.getProductSizes().add(productSizeStock);
                    productStockSum += productSizeStock.getCurrentSizeStock();
                    productSizeStockRepository.save(productSizeStock);
                }
                product.setProductStock(productStockSum);
            }
        } else {
            product.setProductIcon(productInfoDto.getProductIcon());
            product.setProductStock(productInfoDto.getProductStock());
            product.setCategoryType(productInfoDto.getCategoryType());
            product.setProductDescription(productInfoDto.getProductDescription());
            product.setProductName(productInfoDto.getProductName());
            product.setProductPrice(productInfoDto.getProductPrice());
            product.setProductStatus(productInfoDto.getProductStatus());
        }
        return productInfoRepository.save(product);
    }
}
