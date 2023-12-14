package com.example.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.usercenter.service.UserInterfaceInfoService;
import com.example.usercenter.mapper.UserInterfaceInfoMapper;
import org.model.UserInterfaceInfo;
import org.springframework.stereotype.Service;

/**
* @author 余某某
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-07-28 20:30:39
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

}




