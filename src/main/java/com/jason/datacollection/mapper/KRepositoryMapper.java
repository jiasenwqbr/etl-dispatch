package com.jason.datacollection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jason.datacollection.entity.KRepository;

import java.util.List;

public interface KRepositoryMapper extends BaseMapper<KRepository> {

    List<String> selectAllRep();
}
