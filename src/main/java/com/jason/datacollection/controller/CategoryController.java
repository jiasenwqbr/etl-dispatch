package com.jason.datacollection.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KCategory;
import com.jason.datacollection.entity.KScript;
import com.jason.datacollection.entity.QueryHelper;
import com.jason.datacollection.service.KCategoryService;
import com.jason.datacollection.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 分类管理API
 *
 * @author lyf
 */
@Api(tags = "分类管理")
@RequestMapping("/dataCollection/category")
@RestController
public class CategoryController {


    @Autowired
    KCategoryService kCategoryService;

    /**
     * 添加分类
     *
     * @param category {@link KCategory}
     * @return {@link Result}
     */
    @ApiOperation(value = "添加分类")
    @PostMapping("/add")
    public Result add(@RequestBody KCategory category) {
        category.setId(StringUtil.uuid());
        category.setAddTime(new Date());
        boolean result = kCategoryService.save(category);
        if (!result) {
            return Result.error("9999", "新增失败");
        }
        return Result.ok();
    }

    /**
     * 通过id删除分类
     *
     * @param id 要删除的数据的id
     * @return {@link Result}
     */
    @ApiOperation(value = "通过id删除分类")
    @DeleteMapping("/delete")
    Result delete(@RequestParam("id") String id) {
        boolean result = kCategoryService.removeById(id);
        if (!result) {
            return Result.error("9999", "删除失败");
        }
        return Result.ok();
    }

    /**
     * 批量删除分类
     *
     * @param ids 要删除数据的{@link List}集
     * @return {@link Result}
     */
    @ApiOperation(value = "批量删除分类")
    @DeleteMapping("/deleteBatch")
    Result deleteBatch(@RequestBody List<String> ids) {
        boolean result = kCategoryService.removeByIds(ids);
        if (!result) {
            return Result.error("9999", "删除失败");
        }
        return Result.ok();
    }

    /**
     * 更新分类
     *
     * @param category {@link KCategory}
     * @return {@link Result}
     */
    @ApiOperation(value = "更新分类")
    @PutMapping("/update")
    Result update(@RequestBody KCategory category) {
        boolean result = kCategoryService.updateById(category);
        if (!result) {
            return Result.error("9999", "更新失败");
        }
        return Result.ok();
    }

    /**
     * 根据条件查询分类列表
     * @param kCategoryQueryHelper
     * @return
     */
    @ApiOperation(value = "根据条件查询分类列表")
    @PostMapping("/findCategoryListByPage")
    Result<Page<KCategory>> findCategoryListByPage(@RequestBody QueryHelper<KCategory> kCategoryQueryHelper) {
        return Result.ok(kCategoryService.findCategoryListByPage(kCategoryQueryHelper.getQuery(), kCategoryQueryHelper.getPage(), kCategoryQueryHelper.getRows()));
    }

    /**
     * 查询分类明细
     *
     * @param id 根据id查询
     * @return {@link Result}
     */
    @ApiOperation(value = "查询分类明细")
    @GetMapping("/getCategoryDetail")
    Result<KCategory> getCategoryDetail(@RequestParam("id") String id) {
        KCategory category = kCategoryService.getById(id);
        return Result.ok(category);
    }

    /**
     * 查询分类列表
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "查询分类列表")
    @GetMapping("/findCategoryList")
    Result<List<KCategory>> findCategoryList() {
        List<KCategory> list = kCategoryService.list();
        return Result.ok(list);
    }

    /**
     * 验证分类名是否存在
     *
     * @param categoryName 分类名
     * @return 只能返回true或false
     */
    @ApiOperation(value = "验证分类名是否存在")
    @PostMapping("/categoryExist")
    boolean categoryExist(String categoryName) {
        boolean result = true;
        QueryWrapper<KCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name", categoryName);
        int count = kCategoryService.count(queryWrapper);
        if (count > 0) {
            result = false;
        }
        return result;
    }

    /**
     * 根据条件查询转换列表
     *
     * @param cid
     * @param name
     * @param pageNum
     * @param pageSize
     * @param type
     * @return
     */
    @ApiOperation(value = "根据条件查询转换列表 TYPE=1为查询转换，2为查询作业,默认查询全部")
    @GetMapping("/findListByPageSimple")
    Result<Object> findListByPageSimple(@RequestParam("cid") String cid,
                                        @RequestParam(name = "name", required = false) String name,
                                        @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                        @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(name = "type") Integer type) {
        Page<KScript> list = kCategoryService.findListByPageSimple(cid, name, pageNum, pageSize, type);
        return Result.ok(list);
    }

}
