package com.example.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usercenter.model.dto.UserRegisterRequest;
import com.example.usercenter.model.vo.UserVO;
import org.model.User;


import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface UserService extends IService<User> {
    /**
     * 用户注册
     * 用户注册信息的封装类  包括用户名 账号 密码 QQ等基本信息
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    String userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * 通过请求头携带的token获取用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User user);

    /**
     * 用户注销
     * @return
     */
    boolean userLogout();
    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 验证用户是否存在
     * @param userAccount  用户账号 下面是邮箱
     * @return
     */
    boolean verifyUserAccount(String userAccount);

    /**
     * 给指定邮箱发送验证码
     * @param email
     * @return
     */
    boolean sendEmail(String email,String userAccount);

    /**
     * 重置密码
     * @param password1
     * @param password2
     * @param userAccount
     * @return
     */
    boolean resettingPassword(String password1,String password2,String userAccount);

    boolean verifyEmail(String code);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);


     List<UserVO> getUserVO(List<User> userList);
}
