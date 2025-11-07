package com.hosiky.security.handle;


import com.alibaba.fastjson2.JSON;
import com.hosiky.common.ResponseStatusEnum;
import com.hosiky.common.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录失败处理器
 */
@Component
public class MyLoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Result result = Result.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(JSON.toJSONString(result));
    }
}
