package com.hosiky.security.handle;

import com.alibaba.fastjson.JSON;
import com.hosiky.common.ResponseStatusEnum;
import com.hosiky.common.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 *  未认证或者认证错误 处理器
 */

@Slf4j
public class MyAuthenticationHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Result result = Result.errorCustom(ResponseStatusEnum.UN_LOGIN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(JSON.toJSONString(result));
    }
}
