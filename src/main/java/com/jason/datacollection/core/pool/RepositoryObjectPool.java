package com.jason.datacollection.core.pool;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * @ClassName RepositoryObjectPool
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/9 10:54
 **/
public class RepositoryObjectPool  extends GenericKeyedObjectPool<String, RepositoryObject> {
    public RepositoryObjectPool(KeyedPooledObjectFactory<String, RepositoryObject> factory) {
        super(factory);
    }

    public RepositoryObjectPool(KeyedPooledObjectFactory<String, RepositoryObject> factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }
}
