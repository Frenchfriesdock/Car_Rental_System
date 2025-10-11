package com.hosiky.security.handle;

import com.alibaba.fastjson.JSON;
import com.hosiky.common.ResponseStatusEnum;
import com.hosiky.common.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 *  认证用户权限不足处理器
 */
@Slf4j
public class MyUserAccessHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result result = Result.errorCustom(ResponseStatusEnum.NO_AUTH);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(JSON.toJSONString(result));
    }
}
