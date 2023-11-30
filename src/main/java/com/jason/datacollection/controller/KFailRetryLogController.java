package com.jason.datacollection.controller;

import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KFailRetryLog;
import com.jason.datacollection.service.KFailRetryLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @auther chen1
 * @create 2022-04-05 18:27
 */
@RestController
@RequestMapping("dataCollection/kFailRetryLog")
@Api(tags = "失败重试控制器")
public class KFailRetryLogController {

    @Autowired
    KFailRetryLogService kFailRetryLogService;

    @GetMapping("getLogByRecord")
    @ApiOperation("获取日志")
    public Result getLogByRecord(@RequestParam("recordId") String recordId) {
        QueryWrapper<KFailRetryLog> repWrapper = new QueryWrapper<>();
        repWrapper.eq("FAIL_RETRY_RECORD_ID", recordId);
        KFailRetryLog log = kFailRetryLogService.getOne(repWrapper);
        return Result.ok(log);
    }
}
