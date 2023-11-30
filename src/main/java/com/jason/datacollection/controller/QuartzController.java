package com.jason.datacollection.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KQuartz;
import com.jason.datacollection.entity.QueryHelper;
import com.jason.datacollection.service.KQuartzService;
import com.jason.datacollection.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 定时任务管理API
 *
 * @author lyf
 */
@Api(tags = "定时任务管理")
@RequestMapping("/dataCollection/quartz")
@RestController
public class QuartzController {

    @Autowired
    KQuartzService quartzService;

    /**
     * 添加定时任务
     *
     * @param quartz {@link KQuartz}
     * @return {@link Result}
     */
    @ApiOperation(value = "添加定时任务")
    @PostMapping("/add")
    Result add(@RequestBody KQuartz quartz) {
        quartz.setId(StringUtil.uuid());
        quartz.setAddTime(new Date());
        boolean result = quartzService.save(quartz);
        if (!result) {
            return Result.error("9999", "新增失败");
        }
        return Result.ok();
    }

    /**
     * 通过id删除定时任务
     *
     * @param id 要删除的数据的id
     * @return {@link Result}
     */
    @ApiOperation(value = "通过id删除定时任务")
    @DeleteMapping("/delete")
    Result delete(@RequestParam("id") String id) {
        boolean result = quartzService.removeById(id);
        if (!result) {
            return Result.error("9999", "删除失败");
        }
        return Result.ok();
    }

    /**
     * 批量删除定时任务
     *
     * @param ids 要删除数据的{@link List}集
     * @return {@link Result}
     */
    @ApiOperation(value = "批量删除定时任务")
    @DeleteMapping("/deleteBatch")
    Result deleteBatch(@RequestBody List<String> ids) {
        boolean result = quartzService.removeByIds(ids);
        if (!result) {
            return Result.error("9999", "删除失败");
        }
        return Result.ok();
    }

    /**
     * 更新定时任务
     *
     * @param quartz {@link KQuartz}
     * @return {@link Result}
     */
    @ApiOperation(value = "更新定时任务")
    @PutMapping("/update")
    Result update(@RequestBody KQuartz quartz) {
        boolean result = quartzService.updateById(quartz);
        if (!result) {
            return Result.error("9999", "更新失败");
        }
        return Result.ok();
    }

    /**
     * 根据条件查询定时任务列表
     * @param kQuartzQueryHelper
     * @return
     */
    @ApiOperation(value = "根据条件查询定时任务列表")
    @PostMapping("/findQuartzListByPage")
    Result<Page<KQuartz>> findQuartzListByPage(@RequestBody QueryHelper<KQuartz> kQuartzQueryHelper) {
        Page<KQuartz> quartzList = quartzService.findListByPage(kQuartzQueryHelper.getQuery(), kQuartzQueryHelper.getPage(), kQuartzQueryHelper.getRows());
        return Result.ok(quartzList);
    }

    /**
     * 查询定时任务明细
     *
     * @param id 根据id查询
     * @return {@link Result}
     */
    @ApiOperation(value = "查询定时任务明细")
    @GetMapping("/getQuartzDetail")
    Result<KQuartz> getQuartzDetail(@RequestParam("id") String id) {
        KQuartz quartz = quartzService.getById(id);
        return Result.ok(quartz);
    }

    /**
     * 查询定时任务列表
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "查询定时任务列表")
    @GetMapping("/findQuartzList")
    Result<List<KQuartz>> findQuartzList() {
        List<KQuartz> list = quartzService.list();
        return Result.ok(list);
    }
}
