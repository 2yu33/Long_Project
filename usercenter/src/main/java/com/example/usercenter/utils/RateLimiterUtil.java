package com.example.usercenter.utils;

import com.example.usercenter.exception.BusinessException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 控制程序在一分钟之后才允许访问
 */
public class RateLimiterUtil {
    private  LocalDateTime lastAccessTime;
    private static final int LIMIT_DURATION_SECONDS = 60; // 限制的时间间隔，单位为秒


    public  void getRemainingSeconds() {
        LocalDateTime currentTime = LocalDateTime.now();
//        如果是第一次访问那么设置它为当前时间的六十秒之后
       if(lastAccessTime == null){
            lastAccessTime = currentTime.plusSeconds(LIMIT_DURATION_SECONDS);
       }

        if (currentTime.isBefore(lastAccessTime)) {
            int time = (int) ChronoUnit.SECONDS.between(currentTime,lastAccessTime);
            throw new BusinessException(40000,"请求过于频繁，请在"+time+"秒后继续操作");
        } else {
//            表示已经过了六十秒那么我们需要重新给六十秒后赋值
            lastAccessTime = currentTime;

        }
    }


}
