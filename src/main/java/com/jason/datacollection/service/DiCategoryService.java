package com.jason.datacollection.service;

import com.jason.datacollection.core.povo.TreeDTO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.DiCategory;
import com.jason.datacollection.entity.DiRespository;

import java.util.List;

public interface DiCategoryService extends IService<DiCategory> {
    Result<Object> delete(String dirId, String repId) throws Exception;

    void update(JSONObject json) throws Exception;

    List<TreeDTO<String>> findDiCateryByRep(String repId) throws Exception;

    DiRespository findRepositoryById(String id);

    Result add(JSONObject json) throws Exception;


}
