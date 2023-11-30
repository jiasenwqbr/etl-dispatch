package com.jason.datacollection.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.entity.KScriptRecord;
import com.jason.datacollection.mapper.KScriptRecordMapper;
import com.jason.datacollection.service.KScriptRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KScriptRecordServiceImpl extends ServiceImpl<KScriptRecordMapper, KScriptRecord> implements KScriptRecordService
{
    @Autowired
    KScriptRecordMapper kScriptRecordMapper;

    @Override
    public PageInfo<KScriptRecord> findTransRecordList(KScriptRecord kScriptRecord, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        List<KScriptRecord> scriptRecords = kScriptRecordMapper.selectAllBySid(kScriptRecord);
        PageInfo<KScriptRecord> pageInfo = new PageInfo<>(scriptRecords);
        return pageInfo;
    }

    @Override
    public List<KScriptRecord> selectErrorList(KScriptRecord kScriptRecord) {
        return kScriptRecordMapper.selectErrorList(kScriptRecord);
    }

    @Override
    public List<Map> get7DayScriptRunstatusForMysql() {
        return kScriptRecordMapper.get7DayScriptRunstatusForMysql();
    }
}
