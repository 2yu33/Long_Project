package com.example.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 */
@Configuration
@Data
public class RedissonConfig {
    private String host= "localhost";
    private String port= "6379";
    @Bean
    public RedissonClient redissonClient() {
// 1. 创建配置对象
        Config config = new Config();
        String Address = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(Address).setDatabase(1);
        // 2. 创建redisson实例

        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;

    }


}

