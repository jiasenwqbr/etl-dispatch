package com.jason.datacollection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jason.datacollection.entity.KScript;

import java.util.List;
public interface KScriptMapper extends BaseMapper<KScript> {

      List<KScript> selectAll(KScript kScript);

      //KScript selectBySid(String id);

      List<KScript> taskCount();

}
