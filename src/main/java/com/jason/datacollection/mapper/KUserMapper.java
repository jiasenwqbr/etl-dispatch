package com.jason.datacollection.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jason.datacollection.entity.KUser;

public interface KUserMapper extends BaseMapper<KUser> {
    int deleteByPrimaryKey(String id);

    int insert(KUser record);

    int insertSelective(KUser record);

    KUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(KUser record);

    int updateByPrimaryKey(KUser record);
}