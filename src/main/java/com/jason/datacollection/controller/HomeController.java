package com.jason.datacollection.controller;

import com.alibaba.fastjson.JSONObject;
import com.jason.datacollection.configuration.MyBatisPlusConfig;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KScript;
import com.jason.datacollection.service.KScriptRecordService;
import com.jason.datacollection.service.KScriptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = "首页统计API")
@RestController
@RequestMapping("/dataCollection/monitor")
public class HomeController {

    @Autowired
    KScriptService scriptService;

    @Autowired
    KScriptRecordService scriptRecordService;

    @Autowired
    MyBatisPlusConfig myBatisPlusConfig;

    /**
     * 首页监控任务统计
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "首页监控任务统计")
    @PostMapping("/taskCount")
    public Result<JSONObject> taskCount() {
        Integer transTaskNum = 0;
        Integer jobTaskNum = 0;
        Integer totalTaskNum = 0;
        List<KScript> list = scriptService.taskCount();
        for (int i = 0; i < list.size(); i++) {
            KScript script = list.get(i);
            if (script.getScriptType() == "1" || script.getScriptType().equals("1")) {
                // 统计运行的转换数量
                transTaskNum = script.getNum();
            } else if (script.getScriptType() == "0" || script.getScriptType().equals("0")) {
                // 统计运行的作业数量
                jobTaskNum = script.getNum();
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalTaskNum", transTaskNum + jobTaskNum);
        jsonObject.put("transTaskNum", transTaskNum);
        jsonObject.put("jobTaskNum", jobTaskNum);
        return Result.ok(jsonObject);
    }

    /**
     * 首页监控任务统计
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "首页监控任务统计")
    @PostMapping("/runStatus")
    public Result<List<Map>> runStatus() {
        List<Map> map = new ArrayList<>();
        if (myBatisPlusConfig.getDbType().getDb().equals("mysql")) {
            map = scriptRecordService.get7DayScriptRunstatusForMysql();
        }else{

        }
        return Result.ok(map);
    }
}
