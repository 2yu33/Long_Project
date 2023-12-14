package com.example.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.mapper.UserMapper;
import com.example.usercenter.model.dto.LoginUser;
import org.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", username);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该用户不存在");
        System.out.println(user.getUserPassword());
        return new LoginUser(user);
    }
}
