package com.jason.datacollection.controller;

import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.DiScript;
import com.jason.datacollection.service.DiScriptService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "资源库脚本管理API")
@RequestMapping("/dataCollection/discript")
@RestController
public class DiScriptApiController {

    @Autowired
    DiScriptService diScriptService;

    /**
     * 添加脚本
     *
     * @param json {@link }
     * @return {@link Result}
     */
    @ApiOperation(value = "添加脚本")
    @PostMapping("/add")
    public Result add(@RequestBody JSONObject json) throws Exception {
        return diScriptService.add(json);
    }


    /**
     * 删除转换
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "通过id删除资源库脚本")
    @DeleteMapping("/delete")
    public Result delete(String repId, String id, String type) throws Exception {
        diScriptService.delete(repId, id, type);
        return Result.ok();
    }

    /**
     * 更新资源库
     *
     * @param diScript {@link DiScript}
     * @return {@link Result}
     */
    @ApiOperation(value = "更新资源库脚本")
    @PutMapping("/update")
    public Result update(@RequestBody JSONObject jsonObject) throws Exception {
        // 修改
        diScriptService.update(jsonObject);
        return Result.ok();
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源库目录下的脚本")
    @GetMapping("/findDiScriptById")
    public Result findDiScriptByCatagoryId(@RequestParam("id") String id,
                                           @RequestParam("repId") String repId,
                                           @RequestParam("page") Integer pageNum,
                                           @RequestParam("pageSize") Integer pageSize) {
        return diScriptService.findDiScriptByCatagoryId(id, repId, pageNum, pageSize);
    }

    /**
     * @param diScript
     * @return
     */
    @ApiOperation(value = "移动资源库目录下的脚本到其他目录")
    @PostMapping("/moveScript")
    public Result moveScript(@RequestBody JSONObject jsonObject) throws Exception {
        return diScriptService.moveScript(jsonObject);
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源库下指定目录的转换和作业")
    @GetMapping("/findTransAndJobById")
    public Result<Map<String, Integer>> findTransAndJobById(String id) {
        return Result.ok(diScriptService.findTransAndJobById(id));
    }
}
