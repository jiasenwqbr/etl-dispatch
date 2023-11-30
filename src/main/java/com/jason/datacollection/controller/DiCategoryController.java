package com.jason.datacollection.controller;

import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.DiCategory;
import com.jason.datacollection.entity.DiRespository;
import com.jason.datacollection.service.DiCategoryService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 资源库管理
 *
 * @author lyf
 */
@Api(tags = "资源库目录管理API")
@RequestMapping("/dataCollection/dicategory")
@RestController
public class DiCategoryController {
    @Autowired
    DiCategoryService diCategoryService;

    /**
     * 添加目录
     *
     * @param json
     * @return 添加结果
     */
    @ApiOperation(value = "添加目录")
    @PostMapping("/add")
    public Result add(@RequestBody JSONObject json) throws Exception {
        return diCategoryService.add(json);
    }

    /**
     * 删除资源库目录并删除存在oracle的数据
     *
     * @param dirId
     * @param repId
     * @return {@link Result}
     */
    @ApiOperation(value = "通过id删除资源库目录")
    @GetMapping("/delete")
    public Result delete(@RequestParam("dirId") String dirId, String repId) throws Exception {
        return diCategoryService.delete(dirId, repId);
    }

    /**
     * 更新资源库目录
     *
     * @param jsonObject
     * @return {@link Result}
     */
    @ApiOperation(value = "更新资源库目录")
    @PostMapping("/update")
    public Result update(@RequestBody JSONObject jsonObject) throws Exception {
        // 修改
        diCategoryService.update(jsonObject);
        return Result.ok();
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源库目录树")
    @GetMapping("/findDiCateryByRep")
    public Result findDiCateryByRep(@RequestParam("id") String id) throws Exception {
        // 查询
        return Result.ok(diCategoryService.findDiCateryByRep(id));
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源库目录")
    @GetMapping("/findDiCateryById")
    public Result<DiCategory> findDiCateryById(@RequestParam("id") String id) {
        return Result.ok(diCategoryService.getById(id));
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源库信息")
    @GetMapping("/findRepositoryById")
    public Result<DiRespository> findRepositoryById(@RequestParam("id") String id) {
        return Result.ok(diCategoryService.findRepositoryById(id));
    }


}
