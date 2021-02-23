package com.nemanjav.back.service;

import com.nemanjav.back.dto.ProductInfoDto;
import com.nemanjav.back.entity.ProductInfo;
import com.nemanjav.back.enums.ProductStatusEnum;
import com.nemanjav.back.enums.ResultEnum;
import com.nemanjav.back.exception.MyException;
import com.nemanjav.back.repository.ProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final ProductInfoRepository productInfoRepository;

    private final CategoryService categoryService;

    public ProductInfo findOne(Long productId){
        ProductInfo existingProduct = productInfoRepository.findByProductId(productId);
        if(existingProduct == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }else{
            return existingProduct;
        }
    }

    public Page<ProductInfo> findAllUpProducts(Pageable pageable){
            return productInfoRepository.findAllByProductStatusOrderByProductIdAsc(ProductStatusEnum.UP.getCode() , pageable);
    }

    public Page<ProductInfo> findAllProducts(Pageable pageable) {
        return productInfoRepository.findAllByOrderByProductId(pageable);
    }

    public Page<ProductInfo> findAllInCategory(Integer categoryType , Pageable pageable){
        return productInfoRepository.findAllByCategoryTypeOrderByProductIdAsc(categoryType, pageable);
    }


    // ADMIN
    @Transactional
    public void changeStock(Long productId , Integer amount){
        ProductInfo existingProduct = findOne(productId);
        if(existingProduct == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        existingProduct.setProductStock(amount);
        productInfoRepository.saveAndFlush(existingProduct);
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
    public void decreaseStock(Long productId, int amount) {
        ProductInfo productInfo = findOne(productId);
        if (productInfo == null) throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);

        int update = productInfo.getProductStock() - amount;
        if(update <= 0) throw new MyException(ResultEnum.PRODUCT_NOT_ENOUGH );

        productInfo.setProductStock(update);
        productInfoRepository.saveAndFlush(productInfo);
    }

    @Transactional
    public ProductInfo onSale(Long productId){
        ProductInfo existingProduct = findOne(productId);
        if(existingProduct == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if(existingProduct.getProductStatus().equals(ProductStatusEnum.UP.getCode())){
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
    public ProductInfo update(Long productId , ProductInfoDto productInfoDto) throws IOException {
        // if null throw exception
        ProductInfo productInfo = findOne(productId);
        if(productInfo == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if(productInfo.getProductStatus() > 1) {
            throw new MyException(ResultEnum.PRODUCT_STATUS_ERROR);
        }
        if(productInfoDto.getProductIcon() != null){
            productInfo.setProductIcon(productInfoDto.getProductIcon());
        }
        if(productInfoDto.getProductStock() != null){
            productInfo.setProductStock(productInfoDto.getProductStock());
        }
        if(productInfoDto.getCategoryType() != null){
            productInfo.setCategoryType(productInfoDto.getCategoryType());
        }
        if(productInfoDto.getProductDescription() != null){
            productInfo.setProductDescription(productInfoDto.getProductDescription());
        }
        if(productInfoDto.getProductName() != null){
            productInfo.setProductName(productInfoDto.getProductName());
        }
        if(productInfoDto.getProductPrice() != null){
            productInfo.setProductPrice(productInfoDto.getProductPrice());
        }
        if(productInfoDto.getProductStatus() != null){
            productInfo.setProductStatus(productInfoDto.getProductStatus());
        }

        return productInfoRepository.saveAndFlush(productInfo);
    }

    @Transactional
    public void deleteProduct(Long productId){
        ProductInfo productInfo = findOne(productId);
        if(productInfo == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }else{
            productInfoRepository.delete(productInfo);
        }
    }

    @Transactional
    public ProductInfo save(ProductInfoDto productInfoDto){

        ProductInfo product = new ProductInfo();

        product.setProductIcon(productInfoDto.getProductIcon());
        product.setProductStock(productInfoDto.getProductStock());
        product.setCategoryType(productInfoDto.getCategoryType());
        product.setProductDescription(productInfoDto.getProductDescription());
        product.setProductName(productInfoDto.getProductName());
        product.setProductPrice(productInfoDto.getProductPrice());
        product.setProductStatus(productInfoDto.getProductStatus());

        return productInfoRepository.save(product);
    }

    @Transactional
    public boolean saveImageToProduct(Long productInfoId , MultipartFile file) throws IOException {
        ProductInfo existingProduct = productInfoRepository.findByProductId(productInfoId);
        if(existingProduct == null){
            throw new MyException(ResultEnum.PRODUCT_NOT_EXIST);
        }else{
            System.out.println(file.getOriginalFilename());
            boolean isFileOk = false;
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if(fileName.contains("..")){
                System.out.println("Not a valid file!");
                return false;
            }
            if(fileName.endsWith("png") || fileName.endsWith("jpg")) {
                isFileOk = true;
            }
            if(isFileOk){
            //    existingProduct.setProductIcon(Base64.getEncoder().encode(file.getBytes()));
                productInfoRepository.saveAndFlush(existingProduct);
                return true;
            }else{
                return false;
            }
        }
    }

}
