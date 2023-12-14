package com.example.usercenter.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.stream.Collectors;
import java.util.*;

@Data
public class LoginUser implements UserDetails {
    private User user;
    private List<String> permissions = Arrays.asList("role","admin");
// 不会被序列化
    @JSONField(serialize = false)
    /**
     * 注意 由于这个类可能存在某些敏感信息等无法遇见的问题 所以可能被框架限制了序列化 为了安全导致它无法被序列化，存入
     * 到redis会出错提示无法被序列化 这里我们只需要将permissions序列化，通过它给我们的Set<SimpleGrantedAuthority>
     *     authorizes赋值便可解决 通过注解拒绝对此类(fastjson的方法)进行序列化
     */
    private Set<SimpleGrantedAuthority> authorizes;
    public LoginUser(User user){
        this.user = user;
    }
//    封装权限信息，这里暂时写死
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(authorizes != null)
            return authorizes;
         authorizes = permissions.stream().map(SimpleGrantedAuthority::new).
                collect(Collectors.toSet());
        return authorizes;
    }

    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
