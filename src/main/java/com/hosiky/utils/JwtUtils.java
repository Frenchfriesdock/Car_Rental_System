package com.hosiky.utils;



import com.hosiky.properties.JwtProperties;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成jwt令牌
     */
    public String generateJwt(Map<String, Object> claims) {
//        指定签名的时候使用的签名算法，也就是header部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

//        生成jwt的时间
//        这段代码主要用于计算 JWT (JSON Web Token) 的过期时间，并将过期时间转换为 Date 对象。
        long expMillis = System.currentTimeMillis() + jwtProperties.getTtl();
        Date exp = new Date(expMillis);

//          设置jwt的body
//        JwtBuilder builder = Jwts.builder()
//                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
////                .setClaims(claims): claims 是一个自定义的声明对象，用来存放 JWT 的私有声明，比如用户 ID、角色等信息。这些声明是 JWT 的一部分，用来传递客户端和服务端之间的信息。
//                .setClaims(claims)
////                设置签名使用的算法，和签名使用的秘钥
//                .signWith(signatureAlgorithm, jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8))
////                设置过期时间
//                .setExpiration(exp);
//
////        调用 builder.compact() 方法将配置好的 JWT 构建为一个紧凑的、Base64 编码的字符串表示形式，并将其作为方法的返回值返回。
//        return builder.compact();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(exp)
                .signWith(signatureAlgorithm, jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * token解密
     */

    public Claims parseJWT(String token) {
        try {
//            return Jwts.parser()
//                    .setSigningKey(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8))
//                    .parseClaimsJws(token)
//                    .getBody();
                return  Jwts.parserBuilder()
                        .setSigningKey(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        } catch (ExpiredJwtException e) {
            // Handle expired token case
            System.out.println("Token has expired");
            throw e;
        } catch (JwtException e) {
            // Handle other token parsing exceptions
            System.out.println("Invalid token");
            throw e;
        }
    }

}
