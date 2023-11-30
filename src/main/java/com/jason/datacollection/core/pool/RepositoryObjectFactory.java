package com.jason.datacollection.core.pool;

import com.jason.datacollection.core.repository.DataBaseRepository;
import com.jason.datacollection.entity.KRepository;
import com.jason.datacollection.service.KRepositoryService;
import com.jason.datacollection.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.pentaho.di.repository.AbstractRepository;

/**
 * @ClassName RepositoryObjectFactory
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/9 10:52
 **/
@Slf4j
public class RepositoryObjectFactory implements KeyedPooledObjectFactory<String, RepositoryObject> {
    @Override
    public PooledObject<RepositoryObject> makeObject(String key) throws Exception {
        KRepositoryService repositoryService = SpringContextUtil.getBean(KRepositoryService.class);
        //LRepositoryService lRepositoryService = SpringContextUtil.getBean(LRepositoryService.class);
        KRepository repositoryDetail = repositoryService.getRepositoryDetail(String.valueOf(key));
        AbstractRepository repository = null;
        switch (repositoryDetail.getRepType()){
            case "dbRep":
                repository = new DataBaseRepository().buildRepository(repositoryDetail);
                break;
            case "fileRep":
                break;
            default:
        }
        if (repository == null){
            return null;
        }
        return new DefaultPooledObject<>(new RepositoryObject(repository));
    }

    @Override
    public void destroyObject(String str, PooledObject<RepositoryObject> pooledObject) throws Exception {

    }

    @Override
    public boolean validateObject(String str, PooledObject<RepositoryObject> pooledObject) {
        return false;
    }

    @Override
    public void activateObject(String str, PooledObject<RepositoryObject> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(String str, PooledObject<RepositoryObject> pooledObject) throws Exception {

    }
}
