package com.example.usercenter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis和springboot进行整合的配置 进行简单的存储
 */
@Configuration
public class RedisTemplateConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    /**
     * 默认配置就算本地和6379端口 所以不配也可以正常使用
     * @return
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        //创建 ObjectMapper 对象
        ObjectMapper objectMapper = new ObjectMapper();

// 创建 Jackson2JsonRedisSerializer 对象，并设置 ObjectMapper
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jsonRedisSerializer.setObjectMapper(objectMapper);
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        // 创建 StringRedisSerializer 对象
        RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
        // 设置key的序列化方式为 string
        // 设置 RedisTemplate 的键序列化器和值序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        return  redisTemplate;
    }
}
