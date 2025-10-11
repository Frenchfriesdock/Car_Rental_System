package com.hosiky.security.filter;

import com.alibaba.fastjson.JSON;
import com.hosiky.security.entity.MyUserDetail;
import com.hosiky.utils.JwtUtils;
import com.hosiky.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtUtils.parseJWT(token);
        Long userId = Long.valueOf(claims.get("id").toString());
//         先走redis
        String json = redisUtil.get("User:" + userId + ":" + token);
        System.out.println(json);
        if(json == null) {
            filterChain.doFilter(request, response);
            return;
        }

        /**
         * 这个部分还是不懂
         *用来存储用户的个人信息、角色、权限等
         */
        MyUserDetail myUserDetail = JSON.parseObject(json, MyUserDetail.class);
        if(myUserDetail != null) {

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(myUserDetail, null, myUserDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }

    }
}
