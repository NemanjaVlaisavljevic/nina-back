package com.nemanjav.back.service;

import com.nemanjav.back.email.EmailSender;
import com.nemanjav.back.entity.OrderMain;
import com.nemanjav.back.entity.ProductInOrder;
import com.nemanjav.back.entity.ProductInfo;
import com.nemanjav.back.enums.OrderStatusEnum;
import com.nemanjav.back.enums.ResultEnum;
import com.nemanjav.back.exception.MyException;
import com.nemanjav.back.repository.OrderRepository;
import com.nemanjav.back.repository.ProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductInfoService productInfoService;
    private final ProductInfoRepository productInfoRepository;
    private final EmailSender emailSender;

    public Page<OrderMain> findAll(Pageable pageable){
        return orderRepository.findAllByOrderByOrderStatusAscCreateTimeDesc(pageable);
    }

    public Page<OrderMain> findByStatus(Integer status , Pageable pageable){
        return orderRepository.findAllByOrderStatusOrderByCreateTimeDesc(status , pageable);
    }

    public Page<OrderMain> findByBuyerEmail(String email , Pageable pageable){
        return orderRepository.findAllByBuyerEmailOrderByOrderStatusAscCreateTimeDesc(email , pageable);
    }

    public Page<OrderMain> findByBuyerPhone(String phone , Pageable pageable){
        return orderRepository.findAllByBuyerPhoneOrderByOrderStatusAscCreateTimeDesc(phone , pageable);
    }

    public OrderMain findOne(Long orderId){
        Optional<OrderMain> orderMain = orderRepository.findById(orderId);
        if(orderMain.isPresent()){
            return orderMain.get();
        }else{
            throw new MyException(ResultEnum.ORDER_NOT_FOUND);
        }
    }

    @Transactional
    public OrderMain finish(Long orderId){
        OrderMain currentOrder = findOne(orderId);
        if(!currentOrder.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            throw new MyException(ResultEnum.ORDER_STATUS_ERROR);
        }

        currentOrder.setOrderStatus(OrderStatusEnum.FINISHED.getCode());

        orderRepository.saveAndFlush(currentOrder);
        return orderRepository.findByOrderId(orderId);
    }

    @Transactional
    public OrderMain cancel(Long orderId){
        OrderMain currentOrder = findOne(orderId);
        if(!currentOrder.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            throw new MyException(ResultEnum.ORDER_STATUS_ERROR);
        }

        currentOrder.setOrderStatus(OrderStatusEnum.CANCELED.getCode());
        orderRepository.saveAndFlush(currentOrder);

        Iterable<ProductInOrder> products = currentOrder.getProducts();
        for(ProductInOrder product : products){
            ProductInfo productInfo = productInfoRepository.findByProductId(product.getProductId());
            if(productInfo != null){
                productInfoService.increaseStock(productInfo.getProductId() ,product.getCount());
            }
        }
        emailSender.sendCanceledOrder("ninamarkovic@gmail.com" , buildEmail(currentOrder.getOrderId()));

        return orderRepository.findByOrderId(orderId);
    }

    private String buildEmail(Long orderId) {
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
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Order " + orderId + " has been canceled</span>\n" +
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
                "           <p>Order with id " + orderId + " was canceled by user.</p>" +
                "          " +
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
