package com.nemanjav.back.config;

import com.nemanjav.back.entity.ProductCategory;
import com.nemanjav.back.entity.ProductInfo;
import com.nemanjav.back.entity.User;
import com.nemanjav.back.enums.UserRole;
import com.nemanjav.back.repository.CartRepository;
import com.nemanjav.back.repository.ProductCategoryRepository;
import com.nemanjav.back.repository.ProductInfoRepository;
import com.nemanjav.back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductInfoRepository productInfoRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(productCategoryRepository.count() == 0) {

            ProductCategory productCategory1 = new ProductCategory("ODECA", 0 );
            productCategory1.setCreateTime(new Date());
            productCategory1.setUpdateTime(new Date());
            ProductCategory productCategory2 = new ProductCategory("SOLJE", 1);
            productCategory2.setCreateTime(new Date());
            productCategory2.setUpdateTime(new Date());
            ProductCategory productCategory3 = new ProductCategory("PRIVESCI", 2);
            productCategory3.setCreateTime(new Date());
            productCategory3.setUpdateTime(new Date());

            productCategoryRepository.save(productCategory1);
            productCategoryRepository.save(productCategory2);
            productCategoryRepository.save(productCategory3);
        }

        if(userRepository.count() == 0){
            User adminUser = new User();
            adminUser.setLocked(false);
            adminUser.setPhone("0644577483");
            adminUser.setPassword(passwordEncoder.encode("dotamen5"));
            adminUser.setUserRole(UserRole.ADMIN);
            adminUser.setLastName("Vlaisavljevic");
            adminUser.setEmail("nemanja.vlaisavljevic@hotmail.rs");
            adminUser.setFirstName("Nemanja");
            adminUser.setEnabled(true);
            adminUser.setCity("Bukovac");
            adminUser.setStreetAndNumber("Kralja Petra 1 58B");
            userRepository.save(adminUser);
        }

        if(productInfoRepository.count() == 0){
            ProductInfo productInfo1 = new ProductInfo();
            productInfo1.setProductName("Nike duks");
            productInfo1.setProductPrice(new BigDecimal(9000));
            productInfo1.setProductStock(15);
            productInfo1.setProductDescription("Novi Nike duks 2021 leto" );
            productInfo1.setProductIcon("https://i.ebayimg.com/images/g/qgUAAOSwl2NgBdlv/s-l1600.jpg");
            productInfo1.setCategoryType(0);

            ProductInfo productInfo2 = new ProductInfo();
            productInfo2.setProductName("Adidas duks");
            productInfo2.setProductPrice(new BigDecimal(5000));
            productInfo2.setProductStock(15);
            productInfo2.setProductDescription("Novi Adidas duks 2021 leto" );
            productInfo2.setProductIcon("https://i.ebayimg.com/images/g/GnIAAOSwm8Zd8yeL/s-l1600.jpg");
            productInfo2.setCategoryType(0);

            ProductInfo productInfo3 = new ProductInfo();
            productInfo3.setProductName("Tommy Hilfiger duks");
            productInfo3.setProductPrice(new BigDecimal(11000));
            productInfo3.setProductStock(15);
            productInfo3.setProductDescription("Novi Tommy Hilfiger duks 2021 leto" );
            productInfo3.setProductIcon("https://i.ebayimg.com/images/g/Hh0AAOSwI-BgHIIa/s-l1600.jpg");
            productInfo3.setCategoryType(0);

            ProductInfo productInfo4 = new ProductInfo();
            productInfo4.setProductName("League of legends solja");
            productInfo4.setProductPrice(new BigDecimal(1000));
            productInfo4.setProductStock(15);
            productInfo4.setProductDescription("Nova solja league of legends get it fast" );
            productInfo4.setProductIcon("https://i.ebayimg.com/images/g/VrkAAOSw~tFgCB6p/s-l1600.jpg");
            productInfo4.setCategoryType(1);

            ProductInfo productInfo5 = new ProductInfo();
            productInfo5.setProductName("Sailor moon solja");
            productInfo5.setProductPrice(new BigDecimal(890));
            productInfo5.setProductStock(15);
            productInfo5.setProductDescription("Nova solja sailor moon get it fast" );
            productInfo5.setProductIcon("https://i.ebayimg.com/images/g/-4gAAOSwfKhgCpag/s-l1600.jpg");
            productInfo5.setCategoryType(1);

            ProductInfo productInfo6 = new ProductInfo();
            productInfo6.setProductName("Walking dead solja");
            productInfo6.setProductPrice(new BigDecimal(1200));
            productInfo6.setProductStock(15);
            productInfo6.setProductDescription("Novi solja walking dead get it fast" );
            productInfo6.setProductIcon("https://i.ebayimg.com/images/g/7NsAAOSwJkFd2Fql/s-l1600.jpg");
            productInfo6.setCategoryType(1);

            ProductInfo productInfo7 = new ProductInfo();
            productInfo7.setProductName("Sailor moon privezak 1");
            productInfo7.setProductPrice(new BigDecimal(500));
            productInfo7.setProductStock(15);
            productInfo7.setProductDescription("Novi privesci iz kolekcije Sailor Moon" );
            productInfo7.setProductIcon("https://i.ebayimg.com/images/g/22AAAOSwBUlfVuVQ/s-l1600.jpg");
            productInfo7.setCategoryType(2);

            ProductInfo productInfo8 = new ProductInfo();
            productInfo8.setProductName("Sailor moon privezak 2");
            productInfo8.setProductPrice(new BigDecimal(600));
            productInfo8.setProductStock(15);
            productInfo8.setProductDescription("Novi privesci iz kolekcije Sailor Moon" );
            productInfo8.setProductIcon("https://i.ebayimg.com/images/g/5SoAAOSw0dFcYROm/s-l1600.jpg");
            productInfo8.setCategoryType(2);

            ProductInfo productInfo9 = new ProductInfo();
            productInfo9.setProductName("Sailor moon privezak 3");
            productInfo9.setProductPrice(new BigDecimal(700));
            productInfo9.setProductStock(15);
            productInfo9.setProductDescription("Novi privesci iz kolekcije Sailor Moon" );
            productInfo9.setProductIcon("https://i.ebayimg.com/images/g/1NIAAOSwGUpcEn8z/s-l1600.jpg");
            productInfo9.setCategoryType(2);

            productInfoRepository.save(productInfo1);
            productInfoRepository.save(productInfo2);
            productInfoRepository.save(productInfo3);
            productInfoRepository.save(productInfo4);
            productInfoRepository.save(productInfo5);
            productInfoRepository.save(productInfo6);
            productInfoRepository.save(productInfo7);
            productInfoRepository.save(productInfo8);
            productInfoRepository.save(productInfo9);

            System.out.println("Successfully saved product categories and one product in database ON startup!");
        }
    }
}
