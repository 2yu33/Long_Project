package com.example.usercenter.utils;




import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.usercenter.exception.BusinessException;
import org.model.User;

import java.util.Calendar;
import java.util.Map;

public class JWTUtils {
    /**
     * 生成token   head.payload.sing
     */
    private static final String SIGN = "SW@#123$%$#@&$";
    public static String getToken(User user){

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH,5);
        // head是固定的，可以不写 withClain则是payload 然后设置过期时间 然后设置签名的算法,一般采用Algorithm.HMAC256
        String Token = JWT.create().withSubject(Long.toString(user.getId())).withClaim
                ("userRole",user.getUserRole()).withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SIGN));
        return Token;

    }
    public static boolean verifyToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SIGN))
                    .build()
                    .verify(token);
            return true; // 验证成功
        } catch (JWTVerificationException e) {

            return false; // 验证失败
        }
    }
    public static DecodedJWT decodeToken(String token) {
        try {
            return JWT.decode(token);
        } catch (JWTVerificationException e) {
           throw new BusinessException(40000,"token存在异常!!!");
        }
    }
}
