package com.jason.datacollection.core.pool;

import org.pentaho.di.repository.AbstractRepository;

/**
 * @ClassName RepositoryObject
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/9 10:51
 **/
public class RepositoryObject {
    private AbstractRepository repository;

    private boolean isActive;

    public AbstractRepository getRepository() {
        return repository;
    }

    public void setRepository(AbstractRepository repository) {
        this.repository = repository;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void destroy(){

    }

    public RepositoryObject(AbstractRepository repository) {
        this.repository = repository;
    }

    public RepositoryObject() {}
}
