package com.jason.datacollection.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 描述:
 * 线程任务
 *
 * @author leo
 * @create 2019-09-02 10:40
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Value("${thread.corePoolSize}")
    private int corePoolSize;
    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${thread.queueCapacity}")
    private int queueCapacity;
    @Value("${thread.keepAliveSeconds}")
    private int keepAliveSeconds;

    // 声明一个线程池(并指定线程池的名字)
    @Bean("failRetryTaskExecutor")
    public Executor receiveMessageAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数5：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corePoolSize);
        //最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxPoolSize);
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(queueCapacity);
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("failRetry-");
        executor.initialize();
        return executor;
    }
}
