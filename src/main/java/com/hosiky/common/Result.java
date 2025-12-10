package com.hosiky.common;

import lombok.Data;

import java.util.Map;

@Data
public class Result<T>{

    // 响应业务状态码
    private Integer status;

    // 响应消息
    private String msg;

    // 是否成功
    private Boolean success;

    // 响应数据，可以是Object，也可以是List或Map等
    private Object data;

    public Result(Object data) {
        this.status = ResponseStatusEnum.SUCCESS.status();
        this.msg = ResponseStatusEnum.SUCCESS.msg();
        this.success = ResponseStatusEnum.SUCCESS.success();
        this.data = data;
    }

    /**
     * 成功返回，带有数据的，直接往OK方法丢data数据即可
     * @param data
     * @return
     */
    public static Result ok(Object data) {
        return new Result(data);
    }
    /**
     * 成功返回，不带有数据的，直接调用ok方法，data无须传入（其实就是null）
     * @return
     */
    public static Result ok() {
        return new Result(ResponseStatusEnum.SUCCESS);
    }

    /**
     * 错误返回，直接调用error方法即可，当然也可以在ResponseStatusEnum中自定义错误后再返回也都可以
     * @return
     */
    public static Result error() {
        return new Result(ResponseStatusEnum.FAILED);
    }

    /**
     * 错误返回，map中包含了多条错误信息，可以用于表单验证，把错误统一返回
     * @param map
     * @return
     */
    public static Result errorMap(Map map) {
        return new Result(ResponseStatusEnum.FAILED, map);
    }

    /**
     * 错误返回，直接返回错误的消息
     * @param msg
     * @return
     */
    public static Result errorMsg(String msg) {
        return new Result(ResponseStatusEnum.FAILED, msg);
    }

    /**
     * 错误返回，token异常，一些通用的可以在这里统一定义
     * @return
     */
    public static Result errorTicket() {
        return new Result(ResponseStatusEnum.TICKET_INVALID);
    }

    /**
     * 自定义错误范围，需要传入一个自定义的枚举，可以到[ResponseStatusEnum.java[中自定义后再传入
     * @param responseStatus
     * @return
     */
    public static Result errorCustom(ResponseStatusEnum responseStatus) {
        return new Result(responseStatus);
    }
    public static Result exception(ResponseStatusEnum responseStatus) {
        return new Result(responseStatus);
    }

    public Result(ResponseStatusEnum responseStatus) {
        this.status = responseStatus.status();
        this.msg = responseStatus.msg();
        this.success = responseStatus.success();
    }
    public Result(ResponseStatusEnum responseStatus, Object data) {
        this.status = responseStatus.status();
        this.msg = responseStatus.msg();
        this.success = responseStatus.success();
        this.data = data;
    }
    public Result(ResponseStatusEnum responseStatus, String msg) {
        this.status = responseStatus.status();
        this.msg = msg;
        this.success = responseStatus.success();
    }
    public Result() {
        // 默认构造函数
    }

}
