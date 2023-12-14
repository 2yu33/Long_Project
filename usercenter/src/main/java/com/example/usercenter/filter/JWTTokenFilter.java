package com.example.usercenter.filter;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.utils.JWTUtils;
import org.model.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTTokenFilter extends OncePerRequestFilter {
    @Resource
    RedisTemplate redisTemplate;

    /**
     * Security过滤器 如果没有token则正常通过登录注册传入相关参数进行登录校验如果已经登录那么通过解析token获取登录信息传入到登录的相应
     * 参数以此通过token来完成登录校验
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain
            filterChain) throws ServletException, IOException {
//        获取token 后端将生成的token返回给前端然后前端发送请求都需要携带token在请全头中
//        对token进行验证
        String token = request.getHeader("token");
        if(!StringUtils.hasText(token) ){
//    如果token为空那么我们放行 让请求被用户名和密码拦截就行(就算正常的登录注册) 如果非空那么说明用户已经登录过我们需要对token进行验证
            filterChain.doFilter(request,response);
            return;
        }
//        解析token
        DecodedJWT decodedJWT = JWTUtils.decodeToken(token);
        String userId = decodedJWT.getSubject();
//        从redis中获取用户信息
//        可以通过instanceof判断是不是子类
        User user = (User)redisTemplate.opsForValue().get(userId);
        if(user == null)
            throw new BusinessException(40400,"登录状态过期请重新登录");
//        存入到SecurityContextHolder
//        todo 获取权限信息封装到Authentication后面记得补充
        UsernamePasswordAuthenticationToken  authenticationToken= new
                UsernamePasswordAuthenticationToken(user,null,null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request,response);
    }
}
