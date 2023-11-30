package com.jason.datacollection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jason.datacollection.entity.KScriptRecord;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface KScriptRecordService extends IService<KScriptRecord> {
    PageInfo<KScriptRecord> findTransRecordList(KScriptRecord kScriptRecord, Integer page, Integer rows);

    List<KScriptRecord> selectErrorList(KScriptRecord kScriptRecord);

    List<Map>  get7DayScriptRunstatusForMysql();

}
