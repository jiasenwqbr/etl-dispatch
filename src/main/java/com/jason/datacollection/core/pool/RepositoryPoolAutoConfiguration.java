package com.jason.datacollection.core.pool;

import com.jason.datacollection.service.KRepositoryService;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName RepositoryPoolAutoConfiguration
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/9 10:55
 **/
@EnableConfigurationProperties(RepositoryPoolProperties.class)
@Configuration
public class RepositoryPoolAutoConfiguration {

    private final RepositoryPoolProperties poolProperties;

    private RepositoryObjectPool pool;

    @Autowired
    private KRepositoryService repositoryService;

    @Autowired
    public RepositoryPoolAutoConfiguration(RepositoryPoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }

    @ConditionalOnClass({RepositoryObjectPool.class})
    @Bean(name = "RepositoryPool")
    protected RepositoryObjectPool LRepositorySDKPool() {
        RepositoryObjectFactory repositoryFactory = new RepositoryObjectFactory();

        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxIdlePerKey(poolProperties.getMaxIdle());
        config.setMaxTotalPerKey(poolProperties.getMaxTotal());
        config.setMinIdlePerKey(poolProperties.getMinIdle());
        config.setBlockWhenExhausted(true);

        config.setMaxWaitMillis(1000);
        //一定要关闭jmx，不然springboot启动会报已经注册了某个jmx的错误
        config.setJmxEnabled(false);

        pool = new RepositoryObjectPool(repositoryFactory, config);

//        initPool(poolProperties.getInitialSize(), poolProperties.getMaxIdle());
        return pool;
    }


    private void initPool(int initialSize, int maxIdle) {
        if (initialSize <= 0) {
            return;
        }

        int size = Math.min(initialSize, maxIdle);
        for (int i = 0; i < size; i++) {
            try {
                List<String> ids = repositoryService.selectAllRep();
                for (String id : ids) {
                    pool.addObject(id);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
