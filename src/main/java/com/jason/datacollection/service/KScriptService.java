package com.jason.datacollection.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jason.datacollection.entity.KScript;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface KScriptService extends IService<KScript> {

    PageInfo<KScript> findScriptListByPage(KScript script, Integer page, Integer rows);

    //String checkCatch(String repId, String tjId, String type, JSONObject para, Integer isStart) throws KettleException;

    void addKettleTaskD(String repId, String tjId, String type, JSONObject para, Integer isStart) throws Exception;

    void startAllScript();

    void startScript(String id, boolean executeOnce);

    void startCollectionTask(KScript kScript);

    void stopAllScript();

    void stopScript(String id);

    KScript getById(String id);

    List<KScript> taskCount();
}
