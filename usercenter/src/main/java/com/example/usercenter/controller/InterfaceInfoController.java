//package com.example.usercenter.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.example.usercenter.common.BaseResponse;
//import com.example.usercenter.service.InterfaceInfoService;
//import com.example.usercenter.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 接口管理
// *
// * @author <a href="https://github.com/liyupi">程序员路不平</a>
// * @from <a href="https://yupi.icu">编程导航知识星球</a>
// */
//@RestController
//@RequestMapping("/interfaceInfo")
//@Slf4j
//public class InterfaceInfoController {
//
//    @Resource
//    private InterfaceInfoService interfaceInfoService;
//
//    @Resource
//    private UserService userService;
//
//
//
//    // region 增删改查
//
//    /**
//     * 创建
//     *
//     * @param interfaceInfoAddRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/add")
//    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
//                                               HttpServletRequest request) {
//        if (interfaceInfoAddRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo interfaceInfo = new InterfaceInfo();
//        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
//        // 校验
//        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
//        User loginUser = userService.getLoginUser(request);
//        interfaceInfo.setUserId(loginUser.getId());
//        boolean result = interfaceInfoService.save(interfaceInfo);
//        if (!result) {
//            throw new BusinessException(ErrorCode.OPERATION_ERROR);
//        }
//        long newInterfaceInfoId = interfaceInfo.getId();
//        return ResultUtils.success(newInterfaceInfoId);
//    }
//
//    /**
//     * 删除
//     *
//     * @param deleteRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/delete")
//    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
//        if (deleteRequest == null || deleteRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User user = userService.getLoginUser(request);
//        long id = deleteRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if (oldInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        // 仅本人或管理员可删除
//        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean b = interfaceInfoService.removeById(id);
//        return ResultUtils.success(b);
//    }
//
//    /**
//     * 更新
//     *
//     * @param interfaceInfoUpdateRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/update")
//    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
//                                                     HttpServletRequest request) {
//        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo interfaceInfo = new InterfaceInfo();
//        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
//        // 参数校验
//        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
//        User user = userService.getLoginUser(request);
//        long id = interfaceInfoUpdateRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if (oldInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        // 仅本人或管理员可修改
//        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean result = interfaceInfoService.updateById(interfaceInfo);
//        return ResultUtils.success(result);
//    }
//
//    /**
//     * 根据 id 获取
//     *
//     * @param id
//     * @return
//     */
//    @GetMapping("/get")
//    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
//        return ResultUtils.success(interfaceInfo);
//    }
//
//    /**
//     * 获取列表（仅管理员可使用）
//     *
//     * @param interfaceInfoQueryRequest
//     * @return
//     */
//    @AuthCheck(mustRole = "admin")
//    @GetMapping("/list")
//    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
//        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
//        if (interfaceInfoQueryRequest != null) {
//            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
//        }
//        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
//        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
//        return ResultUtils.success(interfaceInfoList);
//    }
//
//    /**
//     * 分页获取列表
//     *
//     * @param interfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @GetMapping("/list/page")
//    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
//        if (interfaceInfoQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
//        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
//        long current = interfaceInfoQueryRequest.getCurrent();
//        long size = interfaceInfoQueryRequest.getPageSize();
//        String sortField = interfaceInfoQueryRequest.getSortField();
//        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
//        String description = interfaceInfoQuery.getDescription();
//        // description 需支持模糊搜索
//        interfaceInfoQuery.setDescription(null);
//        // 限制爬虫
//        if (size > 50) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
//        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
//        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
//                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
//        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
//        return ResultUtils.success(interfaceInfoPage);
//    }
//
//    // endregion
//
//    /**
//     * 发布
//     *
//     * @param idRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/online")
//    @AuthCheck(mustRole = "admin")
//    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
//                                                     HttpServletRequest request) {
//        if (idRequest == null || idRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long id = idRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if (oldInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        // TODO: 2023/7/12   判断该接口是否可以调用
//
//        // 仅管理员可修改(测试通过则允许上线) 这里的更新只会对被赋值的属性赋值
//        InterfaceInfo interfaceInfo = new InterfaceInfo();
//        interfaceInfo.setId(id);
//        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
//        boolean result = interfaceInfoService.updateById(interfaceInfo);
//        return ResultUtils.success(result);
//    }
//
//    /**
//     * 下线
//     *
//     * @param idRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/offline")
//    @AuthCheck(mustRole = "admin")
//    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
//                                                      HttpServletRequest request) {
//        if (idRequest == null || idRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long id = idRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if (oldInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        // 仅本人或管理员可修改
//        InterfaceInfo interfaceInfo = new InterfaceInfo();
//        interfaceInfo.setId(id);
//        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
//        boolean result = interfaceInfoService.updateById(interfaceInfo);
//        return ResultUtils.success(result);
//
//    }
//
//    /**
//     * 测试调用
//     *
//     * @param interfaceInfoInvokeRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/invoke")
//    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
//                                                    HttpServletRequest request) {
//        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long id = interfaceInfoInvokeRequest.getId();
//        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if (oldInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
//        }
//
//        User loginUser = userService.getLoginUser(request);
//        Gson gson = new Gson();
//        LuClientConfig luClientConfig = new LuClientConfig();
//        luClientConfig.setAccessKey("8078919");
//        luClientConfig.setSecretKey("255f0f365ec30466f657d6fdb21ca4dd");
//        HttpClient httpClient = luClientConfig.getHttpClient();
//        com.example.demo.model.User user = gson.fromJson(userRequestParams, com.example.demo.model.User.class);
//        String usernameByPost =httpClient.getNamePost(user);
//        return ResultUtils.success(usernameByPost);
//        /**
//         * 思路  前端申请调用接口的时候 通过路由转发进入网关 在网关进行统一的鉴权和处理 然后执行接口调用
//         * 前端调用接口可以获取到请求地址和请求参数 通过他们就可以进行请求的转发
//         */
//    }
////    调用通用的接口
//    @PostMapping("all/invoke")
//    public BaseResponse<Object> invokeInterface(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
//    HttpServletRequest request) {
//        return ResultUtils.success("avc");
//    }
//}
//
