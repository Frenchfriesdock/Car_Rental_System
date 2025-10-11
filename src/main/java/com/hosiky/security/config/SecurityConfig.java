package com.hosiky.security.config;

import com.hosiky.security.filter.JwtFilter;
import com.hosiky.security.handle.MyAuthenticationHandler;
import com.hosiky.security.handle.MyLoginFailHandler;
import com.hosiky.security.handle.MyUserAccessHandler;
import com.hosiky.utils.JwtUtils;
import com.hosiky.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

/**
 * 密码加密+认证管理器
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyLoginFailHandler loginFailHandler; // 登录失败处理器

    private final ApplicationContext applicationContext;

   private final JwtUtils jwtUtils;


    /**
     * 登录过滤链
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
        commonHttpSetting(http);
        http.securityMatcher("/user/login/**")
                .formLogin(login -> login
                        .failureHandler(loginFailHandler));

        return http.build();
    }

    /**
     * 登录后的权限校验过滤链
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain checkFilterChain(HttpSecurity http) throws Exception {
        commonHttpSetting(http);
        http.securityMatcher("/myApi/**")
                .authorizeRequests(authorize -> authorize.anyRequest().authenticated());

        JwtFilter jwtFilter = new JwtFilter(jwtUtils,applicationContext.getBean(RedisUtil.class));
//        登录之前进行jwt的校验
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 无须登录的过滤链
     * @param http
     * @return
     * @throws Exception
     */

    @Order(1)
    @Bean
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        commonHttpSetting(http);
        http.securityMatcher("/public/**")
                .authorizeRequests(authorize -> authorize.anyRequest().permitAll());
        return http.build();
    }

    /**
     * 密码加密
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 认证管理器
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * 禁用不必要的默认filter，处理异常的响应内容
     * @param http
     * @throws Exception
     */
    private void commonHttpSetting(HttpSecurity http) throws Exception {
        // 表单登录/登出、session管理、csrf防护等默认配置，如果不disable。会默认创建默认filter
        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                // requestCache用于重定向，前后端分析项目无需重定向，requestCache也用不
                .requestCache(cache -> cache
                        .requestCache(new NullRequestCache())
                );

        // 处理 SpringSecurity 异常响应结果。响应数据的结构，改成业务统一的JSON结构。不要框架默认的响应结构
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        // 认证失败异常
                        .authenticationEntryPoint(new MyAuthenticationHandler())
                        // 鉴权失败异常
                        .accessDeniedHandler(new MyUserAccessHandler())
        );
    }
}
