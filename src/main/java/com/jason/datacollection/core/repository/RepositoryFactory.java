package com.jason.datacollection.core.repository;

import com.jason.datacollection.entity.KRepository;
import org.pentaho.di.repository.AbstractRepository;

public interface RepositoryFactory {
    AbstractRepository buildRepository(KRepository lRepository);
}
