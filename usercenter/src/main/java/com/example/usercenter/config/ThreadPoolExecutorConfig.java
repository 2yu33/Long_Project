package com.example.usercenter.config;

import com.sun.istack.internal.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的配置 通过java的并发包juc 这里简单介绍一些线程池参数
 *  corePoolSize(核心线程数 => 可以理解为正式员工) 正确情况下可以同时工作的线程 (也就是就绪状态的线程)
 *  maximumPoolSize 最大线程数 极限状态下线程池中的线程 核心线程满了就是它来即临时工  如果又空闲了那么临时工就可以走了
 *  keepAliveLife(非核心线程存活时间)  非核心线程在没有任务的情况下过多久删除 一般是秒或者分钟
 *  TimeUnit unit(空闲时间存活的单位) 单位秒
 *  workQueue(工作队列) 用于存放给线程执行的任务 存在一个队列的长度(需要设置不可以无限长)
 *  threadFactory(线程工厂) 控制每个线程的生成和线程的属性
 *  RejectedExecutorHandler(拒绝策略): 任务队列满的时候 我们采取什么策略 比如抛异常 不抛异常 自定义策略
 *  比如资源隔离策略
 *  一般任务分为 IO 和计算密集型
 *  计算密集型也就是需要消耗大量CPU资源的一般是音视频处理 图像处理 数学计算等 corePoolSize一般设置为CPU+1(+1是多一个主线程)
 *  可以让每个线程都利用好CPU的每个核 而且线程之间不用频繁切换
 *  I/O密集型 吃带宽/内存磁盘的读写操作 coreSize可以设置的大一点 一般是2n(没有上面的经验好) 还是主要考虑IO的能力
 *
 *
 */
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("线程" + count);
                count++;
                return thread;
            }
        };
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4), threadFactory);
        return threadPoolExecutor;
    }
}

