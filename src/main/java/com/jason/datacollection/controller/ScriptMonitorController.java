package com.jason.datacollection.controller;

import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KScriptMonitor;
import com.jason.datacollection.entity.KScriptRecord;
import com.jason.datacollection.entity.QueryHelper;
import com.jason.datacollection.service.KScriptMonitorService;
import com.jason.datacollection.service.KScriptRecordService;
import com.jason.datacollection.util.FileUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
@Api(tags = "监控管理")
@RequestMapping("/dataCollection/script/monitor")
@RestController
public class ScriptMonitorController {

    @Autowired
    KScriptMonitorService scriptMonitorService;
    @Autowired
    KScriptRecordService scriptRecordService;

    /**
     * 根据条件查询转换监控列表
     *
     * @param req {@link QueryHelper}
     * @return {@link Result}
     */
    @ApiOperation(value = "根据条件查询转换监控列表")
    @PostMapping("/findScriptMonitorListByPage")
    Result<PageInfo<KScriptMonitor>> findTransMonitorListByPage(@RequestBody QueryHelper<KScriptMonitor> req){
     PageInfo<KScriptMonitor> kScriptMonitors = scriptMonitorService.findListByPage(req.getQuery(),req.getPage(),req.getRows());
        return Result.ok(kScriptMonitors);
}
    /**
     * 查询转换id查询转换执行记录
     *
     * @param req 根据转换id查询
     * @return {@link Result}
     */
    @ApiOperation(value = "查询转换监控明细")
    @PostMapping("/findScriptRecordList")
    Result<PageInfo<KScriptRecord>> findTransRecordList(@RequestBody QueryHelper<KScriptRecord> req){
        PageInfo<KScriptRecord> KScriptRecord = scriptRecordService.findTransRecordList(req.getQuery(),req.getPage(),req.getRows());
        return Result.ok(KScriptRecord);
    }

    /**
     * 查看转换执行记录明细
     *
     * @param transRecordId 根据转换记录id查询
     * @return {@link Result}
     */
    @ApiOperation(value = "查看转换执行记录明细")
    @GetMapping("/viewSriptRecordDetail")
    Result<String> viewTransRecordDetail(@RequestParam("transRecordId") String transRecordId){
        KScriptRecord record = scriptRecordService.getById(transRecordId);
        String logContent = "";
        try {
            logContent = FileUtil.readFileToString(new File(record.getLogFilePath()), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok(logContent.replace("\r\n", "<br/>"));
    }
    /**
     * 下载转换执行记录明细
     *
     * @param transRecordId 根据转换记录id查询
     */
    @ApiOperation(value = "下载转换执行记录明细")
    @GetMapping("/downloadTransRecord")
    public void downloadTransRecord(HttpServletResponse response, String transRecordId) {
        // 查询文件路径
        KScriptRecord record = scriptRecordService.getById(transRecordId);
        FileUtil.downloadFile(response, record.getLogFilePath());
    }
    /**
     * 对转换任务执行结果统计
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "对转换任务执行结果统计")
    @GetMapping("/countTrans")
    public Result<Map> countTrans() {
        return Result.ok(scriptMonitorService.countTrans());
    }

    @GetMapping("/findErrorRecordList")
    Result<Object> findErrorRecordList(@RequestParam(value = "categoryId",required = false) String categoryId) {
        KScriptRecord kScriptRecord = new KScriptRecord();
        if(categoryId!=null && categoryId!=""){
            kScriptRecord.setCategoryId(categoryId);
        }
        List<KScriptRecord> kScriptRecords = scriptRecordService.selectErrorList(kScriptRecord);
        return Result.ok(kScriptRecords);
    }
}
