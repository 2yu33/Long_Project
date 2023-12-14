package com.example.usercenter.service;



import com.baomidou.mybatisplus.extension.service.IService;
import org.model.InterfaceInfo;


/**
* @author 余某某
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-07-28 20:30:39
*/
public interface InterfaceInfoService extends IService<InterfaceInfo>{
    void validInterfaceInfo(InterfaceInfo interfaceInfo);
}
