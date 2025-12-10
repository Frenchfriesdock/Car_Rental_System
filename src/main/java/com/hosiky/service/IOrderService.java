package com.hosiky.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.domain.dto.OrderCreateDTO;
import com.hosiky.domain.dto.OrderDTO;
import com.hosiky.domain.po.Order;
import com.hosiky.domain.vo.OrderVO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface IOrderService extends IService<Order> {

    void processOrderTimeout(Long orderId);

    OrderVO createOrder(@Valid OrderCreateDTO orderCreateDTO);

    OrderVO getOrderById(Long orderId);

    IPage getOrderPageByUserId(Long userId, Integer page, Integer size);

    boolean onPaymentSuccess(String orderSn);

    boolean cancelOrderByUser(Long orderId);

    boolean confirmReturn(Long orderId);
}
