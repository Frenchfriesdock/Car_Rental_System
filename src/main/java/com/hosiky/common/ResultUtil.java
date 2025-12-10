package com.hosiky.common;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;

import java.util.UUID;

public class ResultUtil<T> {

    private int code = HttpServletResponse.SC_BAD_REQUEST;
    private String msg;
    private T data;
    private String id = UUID.randomUUID().toString();

    public ResultUtil() {super();}

    public ResultUtil(T data) {this.data = data;}

    public int getCode() {return code;}
    public ResultUtil<T> setCode(int code) {
        this.code = code;
        return this;
    }
    public T getData() {return data;}
    public ResultUtil<T> setData(T data) {this.data = data;return this;}
    public String getMsg() {return msg;}
    public ResultUtil<T> setMsg(String msg) {this.msg = msg;return this;}

    public ResultUtil<T> set(T data, String msg) {this.data = data;this.msg = msg;return this;}

    public ResultUtil<T> set(int code, String msg) {this.code = code;this.msg = msg;return this;}
    public ResultUtil<T> set(int code,T data){
        this.setCode(code);
        this.setData(data);
        return this;
    }
    public ResultUtil<T> set(int code,T data,String msg){
        this.setCode(code);
        this.setData(data);
        this.setMsg(msg);
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static <T> ResultUtil<T> build(int code,T data,String msg, String id){
        ResultUtil<T> result = ResultUtil.build(code, data, msg);
        result.setId(id);
        return result;
    }

    public static <T> ResultUtil<T> build(int code,T data,String msg){
        ResultUtil<T> result = new ResultUtil<T>();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }
    public static <T> ResultUtil<T> build(){
        return new ResultUtil<T>();
    }
    public static <T> ResultUtil<T> build(T data) {
        return new ResultUtil<T>(data);
    }

    public static <T> ResultUtil<T> error (BindingResult bindingResult){
        return ResultUtil.build(HttpServletResponse.SC_BAD_REQUEST, null, bindingResult.getFieldErrors().get(0).getDefaultMessage());
    }

    public static <T> ResultUtil<T> error (String msg){
        return ResultUtil.build(HttpServletResponse.SC_BAD_REQUEST, null, msg);
    }

    public static <T> ResultUtil<T> ok(T data,String msg){
        ResultUtil<T> result = new ResultUtil<T>();
        result.setCode(HttpServletResponse.SC_OK);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

}
