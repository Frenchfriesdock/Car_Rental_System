package com.hosiky.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.component.CancelOrderSender;
import com.hosiky.domain.dto.OrderCreateDTO;
import com.hosiky.domain.dto.OrderDTO;
import com.hosiky.domain.po.Order;
import com.hosiky.domain.vo.OrderVO;
import com.hosiky.mapper.OrderMapper;
import com.hosiky.service.IOrderService;
import com.hosiky.utils.WorkdayCalculatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final CancelOrderSender cancelOrderSender;
    private final OrderMapper orderMapper;

    @Override
    public void processOrderTimeout(Long orderId) {
        try {
            Order order = this.getById(orderId);
            if (order == null) {
                log.error("超时订单不存在: orderId={}", orderId);
            }

            assert order != null;
            if (order.getStatus() == 0) {
                order.setStatus(3);
                order.setUpdatedAt(LocalDateTime.now());
                this.updateById(order);
                log.info("订单超时自动取消成功: orderId={}", orderId);
            } else {
                log.info("订单状态不是'待支付'，不执行超时取消: orderId={}, status={}",
                        orderId, order.getStatus());
            }
        } catch (Exception e) {
            log.error("处理订单超时异常: orderId={}", orderId, e);
            throw new RuntimeException("订单取消处理失败", e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO orderCreateDTO) {
//        参数验证先省略一下

        Order order = new Order();
        BeanUtils.copyProperties(orderCreateDTO, order);
        String sn = generateOrderSn();
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 计算租期信息
        LocalDate startDate = orderCreateDTO.getPickTime().toLocalDate();
        LocalDate endDate = orderCreateDTO.getReturnTime().toLocalDate();

//        计算总得天数，工作日和非工作日
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // 总天数包含起止日
        long workdays = WorkdayCalculatorUtil.calculateWorkdays(startDate, endDate);
        long nonWorkdays = WorkdayCalculatorUtil.calculateNonWorkdays(startDate, endDate);

        order.setDays((int) totalDays);
        order.setStatus(0);
        order.setSn(sn);
        order.setAmount(BigDecimal.valueOf(100 * totalDays));
        order.setDeleted(0);
//        保存订单到数据库里面
        this.save(order);
        log.info("订单创建成功: orderId = {}, orderSn = {}", order.getId(), sn);
//        发送延迟消息,检验订单状态
        try {
            cancelOrderSender.send(order.getId(), 30);
            log.info("订单延迟取消消息发送成功: orderId = {}, orderSn = {}", order.getId(), sn);
        } catch (Exception e) {
            log.error("发送延迟消息失败，需要人工介入: orderId = {}, orderSn = {}", order.getId(), sn, e);
        }

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        return orderVO;
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        return orderVO;
    }

    @Override
    public Page<OrderVO> getOrderPageByUserId(Long userId, Integer page, Integer size) {
        Assert.notNull(userId, "用户ID不能为空");
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 10;

        // 构建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                .eq(Order::getDeleted, 0)
                .orderByDesc(Order::getCreatedAt);

        // 执行分页查询
        Page<Order> entityPage = new Page<>(page, size);
        Page<Order> resultPage = this.page(entityPage, queryWrapper);


        // 使用Lambda表达式进行分页数据转换
//        return resultPage.convert();
        return null;
    }

    @Override
    public boolean onPaymentSuccess(String orderSn) {
        Order order = this.getById(orderSn);
        if (order == null) {
            log.error("支付成功回调失败，订单不存在: orderSn={}", orderSn);
            return false;
        }

        // 只有待支付的订单才能确认支付
        if (order.getStatus() != 0) {
            log.warn("订单当前状态不可支付: orderSn={}, currentStatus={}", orderSn, order.getStatus());
            return false;
        }

        int affected = orderMapper.updateOrderStatus(order.getId(), 1); // 1-已取车（已支付）

        if (affected > 0) {
            log.info("订单支付成功: orderId={}, orderSn={}", order.getId(), orderSn);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelOrderByUser(Long orderId) {

        // 用户主动取消订单（只能取消待支付状态的订单）
        int affected = orderMapper.cancelOrder(orderId);

        if (affected > 0) {
            log.info("用户取消订单成功: orderId={}", orderId);
            return true;
        } else {
            log.warn("用户取消订单失败，订单可能已支付或不存在: orderId={}", orderId);
            return false;
        }
    }

    @Override
    public boolean confirmReturn(Long orderId) {
        // 确认还车业务逻辑
        int affected = orderMapper.confirmReturn(orderId);
        if (affected > 0) {
            log.info("还车确认成功: orderId={}", orderId);
            return true;
        } else {
            log.warn("还车确认失败，车辆可能未取或订单状态异常: orderId={}", orderId);
            return false;
        }
    }

    /**
     * 生成唯一订单号（参考电商系统，但更具租赁特色）
     */
    private String generateOrderSn() {
        return "RC" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderVO convertToVO(Order order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        return vo;
    }

}
