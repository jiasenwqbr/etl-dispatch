package com.jason.datacollection.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jason.datacollection.core.constant.KettleConfig;
import com.jason.datacollection.core.enums.GlobalStatusEnum;
import com.jason.datacollection.core.enums.RunTypeEnum;
import com.jason.datacollection.core.exceptions.MyMessageException;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KScript;
import com.jason.datacollection.entity.KScriptMonitor;
import com.jason.datacollection.entity.KScriptRecord;
import com.jason.datacollection.entity.QueryHelper;
import com.jason.datacollection.service.KScriptMonitorService;
import com.jason.datacollection.service.KScriptRecordService;
import com.jason.datacollection.service.KScriptService;
import com.jason.datacollection.util.FileUtil;
import com.jason.datacollection.util.StringUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * 脚本管理
 *
 * @author lyf
 */
@Api(tags = "脚本管理")
@RequestMapping("/dataCollection/script")
@RestController
public class ScriptController {

    @Autowired
    KScriptService scriptService;

    @Autowired
    KScriptMonitorService scriptMonitorService;

    @Autowired
    KScriptRecordService scriptRecordService;

    /**
     * 添加转换
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "添加转换")
    @PostMapping("/add")
    Result add(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartRequest.getFiles("transFile");
        Enumeration enumeration = request.getParameterNames();
        JSONObject json = new JSONObject();
        while (enumeration.hasMoreElements()) {
            String paramName = enumeration.nextElement().toString();
            String ParamValue = request.getParameter(paramName);
            json.put(paramName, ParamValue);
        }
        //Map<String, String[]> parameterMap = request.getParameterMap();
        KScript script = JSON.toJavaObject(json, KScript.class);
        // 保存上传文件
        script.setId(StringUtil.uuid());
        script.setScriptStatus("2");
        System.out.println(script.getExecuteType());
        if (RunTypeEnum.FILE.getCode().equals(script.getExecuteType())) {
            if (files == null || files.size() == 0) {
                throw new MyMessageException(GlobalStatusEnum.ERROR_PARAM, "上传文件不能为空");
            }
            //获取文件后缀
            String fileName = files.get(0).getOriginalFilename();
            if (fileName.endsWith("ktr")) {
                script.setScriptType("1");
            } else if (fileName.endsWith("kjb")) {
                script.setScriptType("0");
            }
            script.setScriptPath(FileUtil.uploadFile(files.get(0), KettleConfig.uploadPath));
        }
        scriptService.save(script);
        return Result.ok();
    }

    /**
     * 通过id删除转换
     *
     * @param id 要删除的数据的id
     * @return {@link Result}
     */
    @ApiOperation(value = "通过id删除转换")
    @DeleteMapping("/delete")
    Result delete(@RequestParam("id") String id) {
        boolean result = scriptService.removeById(id);
        if (result) {
            //删除监控记录
            QueryWrapper<KScriptMonitor> kScriptMonitorQueryWrapper = new QueryWrapper<>();
            kScriptMonitorQueryWrapper.eq("MONITOR_SCRIPT_ID", id);
            scriptMonitorService.remove(kScriptMonitorQueryWrapper);
            //删除执行记录
            QueryWrapper<KScriptRecord> kScriptRecordQueryWrapper = new QueryWrapper<>();
            kScriptRecordQueryWrapper.eq("RECORD_SCRIPT_ID", id);
            scriptRecordService.remove(kScriptRecordQueryWrapper);
        }
        if (!result) {
            return Result.error("9999", "删除失败");
        }
        return Result.ok();
    }

    /**
     * 批量删除转换
     *
     * @param ids 要删除数据的{@link List}集
     * @return {@link Result}
     */
    @ApiOperation(value = "批量删除转换")
    @DeleteMapping("/deleteBatch")
    Result deleteBatch(@RequestBody List<String> ids) {
        boolean result = scriptService.removeByIds(ids);
        if (!result) {
            return Result.error("9999", "删除失败");
        }
        return Result.ok();
    }

    /**
     * 更新转换
     *
     * @param script {@link KScript}
     * @return {@link Result}
     */
    @ApiOperation(value = "更新转换")
    @PutMapping("/update")
    Result update(@RequestBody KScript script) {
        boolean result = scriptService.updateById(script);
        if (!result) {
            return Result.error("9999", "更新失败");
        }
        return Result.ok();
    }

    @GetMapping("getScriptList")
    @ApiOperation("获取所有脚本（分页）")
    Result<Page<KScript>> getScriptList(@ApiParam(value = "页数", required = false) @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                        @ApiParam(value = "条数", required = false) @RequestParam(value = "rows", required = false, defaultValue = "10") Integer rows,
                                        @ApiParam(value = "分类id", required = false) @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        Page<KScript> scriptPage = scriptService.page(new Page<>(page, rows));
        return Result.ok(scriptPage);
    }

    /**
     * 根据条件查询转换列表
     *
     * @param kScriptQueryHelper
     * @return
     */
    @ApiOperation(value = "根据条件查询转换列表")
    @PostMapping("/findScriptListByPage")
    Result<PageInfo<KScript>> findScriptListByPage(@RequestBody QueryHelper<KScript> kScriptQueryHelper) {
        return Result.ok(scriptService.findScriptListByPage(kScriptQueryHelper.getQuery(), kScriptQueryHelper.getPage(), kScriptQueryHelper.getRows()));
    }

    /**
     * 查询转换明细
     *
     * @param id 根据id查询
     * @return {@link Result}
     */
    @ApiOperation(value = "查询转换明细")
    @GetMapping("/getScriptDetail")
    Result<KScript> getScriptDetail(@RequestParam("id") String id) {
        KScript script = scriptService.getById(id);
        return Result.ok(script);
    }

    /**
     * 全部启动
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "全部启动")
    @GetMapping("/startAllScript")
    Result startAllScript() {
        scriptService.startAllScript();
        return Result.ok();
    }

    /**
     * 单个启动
     *
     * @param id          根据id查询
     * @param executeOnce 是否立即执行 true 立即执行，false 不立即执行
     * @return {@link Result}
     */
    @ApiOperation(value = "单个启动")
    @GetMapping("/startScript")
    Result startScript(@RequestParam("id") String id, @RequestParam(value = "executeOnce", required = false, defaultValue = "false") boolean executeOnce) {
        scriptService.startScript(id, executeOnce);
        return Result.ok();
    }

    /**
     * 全部停止
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "全部停止")
    @GetMapping("/stopAllScript")
    Result stopAllScript() {
        scriptService.stopAllScript();
        return Result.ok();
    }

    /**
     * 单个停止
     *
     * @param id 根据id查询
     * @return {@link Result}
     */
    @ApiOperation(value = "单个停止")
    @GetMapping("/stopScript")
    Result stopScript(@RequestParam("id") String id) {
        scriptService.stopScript(id);
        return Result.ok();
    }

    /**
     * 验证名称是否存在
     *
     * @param scriptName 转换名
     * @return 只能返回true或false
     */
    @ApiOperation(value = "验证名称是否存在")
    @PostMapping("/scriptNameExist")
    String ScriptNameExist(String scriptName) {
        if (StringUtil.isEmpty(scriptName)) {
            return "true";
        } else {
            QueryWrapper<KScript> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("script_name", scriptName);
            KScript script = scriptService.getOne(queryWrapper);
            if (script != null) {
                return "false";
            } else {
                return "true";
            }
        }
    }

    /**
     * 单个启动
     *
     * @param tid 转换id
     * @return 只能返回true或false
     */
    @ApiOperation(value = "单个启动(不通过定时任务，立即执行一次)")
    @GetMapping("/startOne")
    Result startOne(String tid) {
        return null;
    }

    /**
     * 根据批次号查询表名
     *
     * @param batchId 批次id
     */
    @ApiOperation(value = "根据批次号查询表名(根据批次号查询表名)")
    @GetMapping("/selectTableByBatchid")
    Result selectTableByBatchid(@RequestParam("batchId") String batchId) {
        return null;
    }

    /**
     * 根据资源库id，转换id，作业id 判断是否有该任务，没有则新增
     *
     * @param repId 资源库id
     */
    @ApiOperation(value = "根据资源库id，转换id，作业id 判断是否有该任务，没有则新增,type=0为转换，type=1为作业，isStart=0为不执行1为执行 ")
    @PostMapping("/addKettleTask")
    Result addKettleTask(@ApiParam("资源库id") @RequestParam("repId") String repId,
                         @ApiParam("转换作业id") @RequestParam("tjId") String tjId,
                         @ApiParam("type=1为转换，type=2为作业") @RequestParam(name = "type", defaultValue = "1") String type,
                         @ApiParam("是否启动，isStart=0为不启动1为启动") @RequestParam(name = "isStart", required = false, defaultValue = "1") Integer isStart,
                         @ApiParam("参数") @RequestBody JSONObject para) throws KettleException {
        Result<Object> ok = Result.ok();
        try {
            scriptService.addKettleTaskD(repId, tjId, type, para, isStart);
        } catch (Exception e) {
            e.printStackTrace();
            ok.setMessage(e.getMessage());
        }

        return ok;
    }

}
