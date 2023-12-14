package com.example.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserResettingPassword implements Serializable {
    private static final long serialVersionUID = 1493261716372453245L;
    private String userAccount;
    private String password1;
    private String password2;
}
