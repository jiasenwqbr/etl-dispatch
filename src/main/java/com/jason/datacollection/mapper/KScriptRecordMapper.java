package com.jason.datacollection.mapper;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jason.datacollection.entity.KScriptRecord;

import java.util.List;
import java.util.Map;

public interface KScriptRecordMapper extends BaseMapper<KScriptRecord> {
    List<KScriptRecord> selectAllBySid(KScriptRecord kScriptRecord);

    List<KScriptRecord> selectErrorList(KScriptRecord kScriptRecord);

    List<Map> get7DayScriptRunstatusForMysql();

    JSON get7DayScriptRunstatusForOracle();

}
