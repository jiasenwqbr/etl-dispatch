package com.jason.datacollection.core.pool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @ClassName RepositoryPoolProperties
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/9 10:56
 **/
@ConfigurationProperties(prefix = RepositoryPoolProperties.PREFIX)
@EnableConfigurationProperties(RepositoryPoolProperties.class)
public class RepositoryPoolProperties {
    public static final String PREFIX = "kettle.repository";
    /**
     * 最大空闲
     */
    private int maxIdle = 5;
    /**
     * 最大总数
     */
    private int maxTotal = 10;
    /**
     * 最小空闲
     */
    private int minIdle = 2;

    /**
     * 初始化连接数
     */
    private int initialSize = 3;

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }
}
