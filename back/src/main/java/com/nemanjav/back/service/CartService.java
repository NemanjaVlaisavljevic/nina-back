package com.nemanjav.back.service;

import com.nemanjav.back.email.EmailSender;
import com.nemanjav.back.entity.*;
import com.nemanjav.back.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductInfoService productInfoService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductInOrderRepository productInOrderRepository;
    private final ProductInfoRepository productInfoRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final EmailSender emailSender;

    public Cart getCart(User user){
        return user.getCart();
    }

    @Transactional
    public void mergeLocalCart(Collection<ProductInOrder> products , User user){
        Cart currentUserCart = getCart(user);
        products.forEach(productInOrder -> {
            Set<ProductInOrder> currentProducts = currentUserCart.getProducts();
            Optional<ProductInOrder> existingProductSameSize = currentProducts
                    .stream()
                    .filter(productInOrder1 -> productInOrder1.getProductId().equals(productInOrder.getProductId())
                            && productInOrder1.getProductSize().equals(productInOrder.getProductSize()))
                    .findFirst();
            ProductInOrder product;
            if(existingProductSameSize.isPresent()){
                product = existingProductSameSize.get();
                product.setCount(product.getCount() + productInOrder.getCount());
                ProductInfo existingProductInfo = productInfoRepository.findByProductId(product.getProductId());
                if(existingProductInfo != null){
                    existingProductInfo.setProductSize(null);
                    productInfoRepository.saveAndFlush(existingProductInfo);
                }
            }else{
                product = productInOrder;
                product.setCart(currentUserCart);
                currentUserCart.getProducts().add(product);
                ProductInfo existingProductInfo = productInfoRepository.findByProductId(product.getProductId());
                if(existingProductInfo != null){
                    existingProductInfo.setProductSize(null);
                    productInfoRepository.saveAndFlush(existingProductInfo);
                }
            }
            productInOrderRepository.saveAndFlush(product);
        });
            cartRepository.saveAndFlush(currentUserCart);
    }

    @Transactional
    public void delete(Long productInfoId , User user){
        var op = user.getCart()
                .getProducts()
                .stream()
                .filter(productInOrder -> productInfoId.equals(productInOrder.getProductId()))
                .findFirst();
        op.ifPresent(productInOrder -> {
            productInOrder.setCart(null);
            productInOrderRepository.deleteById(productInOrder.getId());
        });
    }

    @Transactional
    public void delete2(Long productInfoId , String productSize , User user){
        var op = user.getCart()
                .getProducts()
                .stream()
                .filter(productInOrder -> productInfoId.equals(productInOrder.getProductId()) && productInOrder.getProductSize().toString().equals(productSize))
                .findFirst();
        op.ifPresent(productInOrder -> {
            productInOrder.setCart(null);
            productInOrderRepository.deleteById(productInOrder.getId());
        });
    }

    @Transactional
    public void checkout(User user){
        OrderMain currentOrder = new OrderMain(user);
        orderRepository.save(currentOrder);
        String products=
                "<table width='100%' border='1' align='center'>"
                        + "<tr align='center'>"
                        + "<td><b>Product Name <b></td>"
                        + "<td><b>Product Size<b></td>"
                        + "<td><b>Product Count<b></td>"
                        + "</tr>";
        for(ProductInOrder product : user.getCart().getProducts()){
            products = products + "<tr align='center'>" + "<td>" + product.getProductName() + "</td>"
                                + "<td>" + product.getProductSize() + "</td>"
                                + "<td>" + product.getCount() + "</td>";
        }

        user.getCart()
                .getProducts()
                .forEach(productInOrder -> {
                    productInOrder.setCart(null);
                    productInOrder.setOrderMain(currentOrder);
                    if(productInOrder.getCategoryType() == 0){
                        productInfoService.decreaseStockForClothes(productInOrder.getProductId() , productInOrder.getCount() , productInOrder.getProductSize());
                    }else{
                        productInfoService.decreaseStock(productInOrder.getProductId() , productInOrder.getCount());
                    }
                    productInOrderRepository.saveAndFlush(productInOrder);
                });

        emailSender.sendOrderDetails("nemanja.vlaisavljevic20@gmail.com",buildEmail(currentOrder , products));

    }

    private String buildEmail(OrderMain currentOrder,String products) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Order " + currentOrder.getOrderId() + " details</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "           <p>Buyer name : " + currentOrder.getBuyerName() + "</p>" +
                "           <p>Buyer email : " + currentOrder.getBuyerEmail() + "</p>" +
                "           <p>Buyer city : " + currentOrder.getBuyerCity() + "</p>" +
                "           <p>Buyer street and number : " + currentOrder.getBuyerStreetAndNumber() + "</p>" +
                "           <p>Buyer phone : " + currentOrder.getBuyerPhone() + "</p>" +
                "           <p>Order amount : " + currentOrder.getOrderAmount() + "</p>" +
                "           <p>Order products : " + products + "</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
