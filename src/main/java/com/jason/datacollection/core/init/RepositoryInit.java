package com.jason.datacollection.core.init;

import com.jason.datacollection.core.pool.RepositoryObjectPool;
import com.jason.datacollection.core.pool.RepositoryPoolProperties;
import com.jason.datacollection.service.KRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName RepositoryInit
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/9 18:19
 **/
@Component
@EnableConfigurationProperties(RepositoryPoolProperties.class)
@Order(99)
@Slf4j
public class RepositoryInit implements ApplicationRunner {

    private final RepositoryPoolProperties poolProperties;

    @Autowired
    KRepositoryService repositoryService;

    @Autowired
    public RepositoryInit(RepositoryPoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }

    @Autowired
    private RepositoryObjectPool pool;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (poolProperties.getInitialSize() <= 0) {
            return;
        }

        int size = Math.min(poolProperties.getInitialSize(), poolProperties.getMaxIdle());
        for (int i = 0; i < size; i++) {
            try {
                List<String> ids = repositoryService.selectAllRep();
                for (String id : ids) {
                    pool.addObject(id);
                }
            } catch (Exception e) {
                log.error("初始化资源库对象池异常，异常信息为:{}", e.getMessage());
            }
        }
    }
}
