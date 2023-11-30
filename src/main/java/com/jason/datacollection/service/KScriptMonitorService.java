package com.jason.datacollection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jason.datacollection.entity.KScriptMonitor;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface KScriptMonitorService extends IService<KScriptMonitor> {
    void updateMonitor(KScriptMonitor scriptMonitor, boolean success);
    PageInfo<KScriptMonitor> findListByPage(KScriptMonitor kScriptMonitor, Integer page, Integer rows);
    Map countTrans();

}
