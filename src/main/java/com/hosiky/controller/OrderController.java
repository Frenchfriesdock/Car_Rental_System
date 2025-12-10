package com.hosiky.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.OrderCreateDTO;
import com.hosiky.domain.vo.OrderVO;
import com.hosiky.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单接口")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Validated
@Slf4j

public class OrderController {

    private final IOrderService orderService;

    @Operation(summary = "创建租车订单")
    @PostMapping
    public Result createOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        try {
            OrderVO orderVO = orderService.createOrder(orderCreateDTO);
            return Result.ok(orderVO);
        } catch (IllegalArgumentException e) {
            return Result.errorMsg("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("创建订单异常", e);
            return Result.errorMsg("订单创建失败，请稍后重试");
        }
    }

    @Operation(summary = "查询订单详情")
    @GetMapping("/{orderId}")
    public Result getOrderById(@PathVariable Long orderId) {
        try {
            OrderVO orderVO = orderService.getOrderById(orderId);
            return Result.ok(orderVO);
        } catch (RuntimeException e) {
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("查询订单异常: orderId={}", orderId, e);
            return Result.errorMsg("查询失败");
        }
    }

    @Operation(summary = "分页查询用户订单")
    @GetMapping("/user/{userId}")
    public Result getOrderPageByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        try {
            Page<OrderVO> result = (Page<OrderVO>) orderService.getOrderPageByUserId(userId, page, size);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("分页查询订单异常: userId={}", userId, e);
            return Result.errorMsg("查询失败");
        }
    }

    @Operation(summary = "支付成功回调")
    @PostMapping("/{orderSn}/payment-success")
    public Result onPaymentSuccess(@PathVariable String orderSn) {
        try {
            boolean success = orderService.onPaymentSuccess(orderSn);
            return success ? Result.ok("支付状态更新成功") : Result.errorMsg("订单可能不存在或状态异常");
        } catch (Exception e) {
            log.error("支付回调处理异常: orderSn={}", orderSn, e);
            return Result.errorMsg("处理支付回调失败");
        }
    }

    @Operation(summary = "用户取消订单")
    @PutMapping("/{orderId}/cancel")
    public Result cancelOrderByUser(@PathVariable Long orderId) {
        try {
            boolean success = orderService.cancelOrderByUser(orderId);
            return success ? Result.ok("订单取消成功") : Result.errorMsg("取消失败，订单可能已支付或不存在");
        } catch (Exception e) {
            log.error("取消订单异常: orderId={}", orderId, e);
            return Result.errorMsg("取消订单失败");
        }
    }

    @Operation(summary = "确认还车")
    @PutMapping("/{orderId}/return")
    public Result confirmReturn(@PathVariable Long orderId) {
        try {
            boolean success = orderService.confirmReturn(orderId);
            return success ? Result.ok("还车确认成功") : Result.errorMsg("还车确认失败，请检查订单状态");
        } catch (Exception e) {
            log.error("还车确认异常: orderId={}", orderId, e);
            return Result.errorMsg("还车确认失败");
        }
    }
}
