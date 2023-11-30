package com.jason.datacollection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.entity.KCategory;
import com.jason.datacollection.entity.KScript;
import com.jason.datacollection.mapper.KCategoryMapper;
import com.jason.datacollection.mapper.KScriptMapper;
import com.jason.datacollection.service.KCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KCategoryServiceImpl extends ServiceImpl<KCategoryMapper, KCategory> implements KCategoryService {

    @Autowired
    KCategoryMapper categoryMapper;

    @Autowired
    KScriptMapper scriptMapper;

    public Page<KCategory> findCategoryListByPage(KCategory category, Integer page, Integer rows) {
        QueryWrapper<KCategory> queryWrapper = new QueryWrapper<>();
        if (category != null) {
            queryWrapper.orderByDesc("edit_time");
            queryWrapper.like("category_name", category.getCategoryName());
        }
        Page<KCategory> result = categoryMapper.selectPage(new Page<KCategory>(page, rows), queryWrapper);
        return result;
    }

    public Page<KScript> findListByPageSimple(String cid, String name, Integer pageNum, Integer pageSize, Integer type) {
        QueryWrapper<KScript> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CATEGORY_ID", cid);
        queryWrapper.like("SCRIPT_NAME", name);
        if(type!=null){
            queryWrapper.eq("SCRIPT_TYPE",type);
        }
        Page<KScript> list = scriptMapper.selectPage(new Page(pageNum, pageSize), queryWrapper);
        return list;
    }
}
