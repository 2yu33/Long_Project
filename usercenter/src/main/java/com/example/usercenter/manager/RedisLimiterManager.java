package com.example.usercenter.manager;

import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 * 通过redis进行限流
 */
@Service
public class RedisLimiterManager {
    @Resource
    RedissonClient redissonClient;
    /**
     * 限流操作
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     * 这里我们采用令牌桶限流 对于相同的key我们指定一段时间发放的令牌
     */
    public void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        // 注意这里的key是用来标识不同业务的，假设你是用户，那么假设key是A那么key是a的一起抢找个令牌！
        /**
         *  为什么要用用户id因为这个限流是针对用户个人的操作，对个人进行限制而不能影响其他人
         *  也许用户不同的业务在不同的场景需要不同的区分，那么我们可以再加一个前缀可以是相应的业务 能区分就行
         */
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //        每秒发放两次令牌
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
