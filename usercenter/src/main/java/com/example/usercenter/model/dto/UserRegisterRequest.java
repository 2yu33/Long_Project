package com.example.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author <a href="https://github.com/liyupi">程序员路不平</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String userName;

    private String QQ;

    private String oldQQ;
}
