package com.example.usercenter.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.mapper.InterfaceInfoMapper;

import com.example.usercenter.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.model.Chart;
import org.model.InterfaceInfo;
import org.springframework.stereotype.Service;

/**
* @author 余某某
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-07-28 20:30:39
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();

        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }
}




