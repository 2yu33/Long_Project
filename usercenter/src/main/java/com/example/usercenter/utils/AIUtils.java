package com.example.usercenter.utils;

import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AIUtils {
    @Resource
    private YuCongMingClient client;
    public String doChat(String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1651468516836098050L);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> devChatResponseBaseResponse = client.doChat(devChatRequest);
        return devChatResponseBaseResponse.getData().getContent();
    }

}
