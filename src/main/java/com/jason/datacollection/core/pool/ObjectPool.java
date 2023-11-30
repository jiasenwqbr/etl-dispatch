package com.jason.datacollection.core.pool;

public interface ObjectPool<T> {
    public ThreadLocal<T>  getObject();

    public void setObject(ThreadLocal<T> obj);
}
