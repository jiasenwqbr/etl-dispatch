package com.jason.datacollection.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.entity.KDatabaseType;
import com.jason.datacollection.mapper.KDatabaseTypeMapper;
import com.jason.datacollection.service.KDatabaseTypeService;
import org.springframework.stereotype.Service;

@Service
public class KDatabaseTypeServiceImpl extends ServiceImpl<KDatabaseTypeMapper, KDatabaseType> implements KDatabaseTypeService {
}
