package com.jason.datacollection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.core.exceptions.MyMessageException;
import com.jason.datacollection.entity.KScriptMonitor;
import com.jason.datacollection.mapper.KScriptMonitorMapper;
import com.jason.datacollection.mapper.KScriptRecordMapper;
import com.jason.datacollection.service.KScriptMonitorService;
import com.jason.datacollection.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KScriptMonitorServiceImpl extends ServiceImpl<KScriptMonitorMapper, KScriptMonitor> implements KScriptMonitorService {

    @Autowired
    KScriptMonitorMapper scriptMonitorMapper;
    @Autowired
    KScriptRecordMapper kScriptRecordMapper;

    public void updateMonitor(KScriptMonitor scriptMonitor, boolean success) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("MONITOR_SCRIPT_ID", scriptMonitor.getMonitorScriptId());
        KScriptMonitor kScriptMonitor = scriptMonitorMapper.selectOne(queryWrapper);
        kScriptMonitor.setLastExecuteTime(scriptMonitor.getLastExecuteTime());
        kScriptMonitor.setNextExecuteTime(scriptMonitor.getNextExecuteTime());
        if (kScriptMonitor == null) {
            throw new MyMessageException("当前脚本对应的监控对象不存在");
        } else {
            if (success) {
                kScriptMonitor.setMonitorSuccess(kScriptMonitor.getMonitorSuccess()+1);
            } else {
                kScriptMonitor.setMonitorFail(kScriptMonitor.getMonitorFail()+1);
            }
            scriptMonitorMapper.updateById(kScriptMonitor);
        }
    }

    @Override
    public PageInfo<KScriptMonitor> findListByPage(KScriptMonitor kScriptMonitor, Integer page, Integer rows) {
        if(null != kScriptMonitor){
            if (ObjectUtil.isEmpty(kScriptMonitor.getCategoryId())){
                kScriptMonitor.setCategoryId(null);
            }
            if (ObjectUtil.isEmpty(kScriptMonitor.getMonitorStatus())){
                kScriptMonitor.setMonitorStatus(null);
            }
        }
        PageHelper.startPage(page,rows);
        List<KScriptMonitor> scriptMonitors = scriptMonitorMapper.selectAll(kScriptMonitor);
        PageInfo<KScriptMonitor> pageInfo = new PageInfo<>(scriptMonitors);
        return pageInfo;
    }
    @Override
     public Map countTrans() {
        return scriptMonitorMapper.countTrans();
    }
}
