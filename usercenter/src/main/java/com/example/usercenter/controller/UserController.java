package com.example.usercenter.controller;

import com.example.usercenter.common.BaseResponse;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.common.ResultUtils;
;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.model.dto.UserRegisterRequest;
import com.example.usercenter.model.dto.UserResettingPassword;
import com.example.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;

import org.model.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")

public class UserController {
    @Resource
    UserService userService;
    @PostMapping("/login")
    public BaseResponse<String> userLogin(@RequestBody User user, HttpServletRequest request){
        if (user == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String jwt = userService.userLogin(userAccount, userPassword, request);
        if(jwt == null)
            return ResultUtils.error(40000,"你的账号或者密码不正确");
        System.out.println("张三");
        return ResultUtils.success(jwt);

    }
    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        userRegisterRequest.setQQ(userRegisterRequest.getOldQQ());
        if (userRegisterRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }
    @GetMapping("/verifyUserAccount")
    public BaseResponse<Boolean> verifyUserAccount(@RequestParam("userAccount")String userAccount){
        if(userAccount == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean flag = userService.verifyUserAccount(userAccount);
        return ResultUtils.success(flag);

    }
    @GetMapping("/sendEmail")
    public BaseResponse<Boolean> sendEmail(@RequestParam("email")String email,@RequestParam("userAccount")String userAccount){
        boolean flag = userService.sendEmail(email, userAccount);

        return ResultUtils.success(flag);
    }
    @PostMapping("/resettingPassword")
    public BaseResponse<Boolean> resettingPassword(@RequestBody UserResettingPassword userResettingPassword)
           {
        String userAccount = userResettingPassword.getUserAccount();
        String password1 = userResettingPassword.getPassword1();
        String password2 = userResettingPassword.getPassword2();
        boolean flag = userService.resettingPassword(password1, password2, userAccount);
        return ResultUtils.success(flag);
    }
    @GetMapping("/verifyEmail")
    public BaseResponse<Boolean> verifyEmail(@RequestParam("verification")String code){
        boolean flag = userService.verifyEmail(code);
        return ResultUtils.success(flag);
    }
    @GetMapping("/Logout")
    public BaseResponse<String> Logout(){
        if(userService.userLogout())
            return ResultUtils.success(200,null,"登出成功");
        else throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
}
