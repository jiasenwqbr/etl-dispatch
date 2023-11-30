package com.jason.datacollection.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.entity.KQuartz;
import com.jason.datacollection.mapper.KQuartzMapper;
import com.jason.datacollection.service.KQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class KQuartzServiceImpl extends ServiceImpl<KQuartzMapper, KQuartz> implements KQuartzService {

    @Autowired
    KQuartzMapper quartzMapper;

    @Override
    public boolean save(KQuartz entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<KQuartz> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<KQuartz> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Override
    public boolean remove(Wrapper<KQuartz> queryWrapper) {
        return super.remove(queryWrapper);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }

    @Override
    public boolean updateById(KQuartz entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean update(Wrapper<KQuartz> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Override
    public boolean update(KQuartz entity, Wrapper<KQuartz> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Override
    public boolean updateBatchById(Collection<KQuartz> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    public KQuartz getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public List<KQuartz> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    public List<KQuartz> listByMap(Map<String, Object> columnMap) {
        return super.listByMap(columnMap);
    }

    @Override
    public KQuartz getOne(Wrapper<KQuartz> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    public int count() {
        return super.count();
    }

    @Override
    public int count(Wrapper<KQuartz> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    public List<KQuartz> list(Wrapper<KQuartz> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public List<KQuartz> list() {
        return super.list();
    }

    @Override
    public <E extends IPage<KQuartz>> E page(E page, Wrapper<KQuartz> queryWrapper) {
        return super.page(page, queryWrapper);
    }

    @Override
    public <E extends IPage<KQuartz>> E page(E page) {
        return super.page(page);
    }

    @Override
    public List<Map<String, Object>> listMaps(Wrapper<KQuartz> queryWrapper) {
        return super.listMaps(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        return super.listMaps();
    }

    @Override
    public List<Object> listObjs() {
        return super.listObjs();
    }

    @Override
    public <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return super.listObjs(mapper);
    }

    @Override
    public List<Object> listObjs(Wrapper<KQuartz> queryWrapper) {
        return super.listObjs(queryWrapper);
    }

    @Override
    public <V> List<V> listObjs(Wrapper<KQuartz> queryWrapper, Function<? super Object, V> mapper) {
        return super.listObjs(queryWrapper, mapper);
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<KQuartz> queryWrapper) {
        return super.pageMaps(page, queryWrapper);
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page) {
        return super.pageMaps(page);
    }

    @Override
    public QueryChainWrapper<KQuartz> query() {
        return super.query();
    }

    @Override
    public LambdaQueryChainWrapper<KQuartz> lambdaQuery() {
        return super.lambdaQuery();
    }

    @Override
    public KtQueryChainWrapper<KQuartz> ktQuery() {
        return super.ktQuery();
    }

    @Override
    public KtUpdateChainWrapper<KQuartz> ktUpdate() {
        return super.ktUpdate();
    }

    @Override
    public UpdateChainWrapper<KQuartz> update() {
        return super.update();
    }

    @Override
    public LambdaUpdateChainWrapper<KQuartz> lambdaUpdate() {
        return super.lambdaUpdate();
    }

    @Override
    public boolean saveOrUpdate(KQuartz entity, Wrapper<KQuartz> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }


    public Page<KQuartz> findListByPage(KQuartz quartz, Integer page, Integer rows) {
        QueryWrapper<KQuartz> queryWrapper = new QueryWrapper<>();
        if (quartz != null) {
            queryWrapper.orderByDesc("edit_time");
            queryWrapper.like("QUARTZ_DESCRIPTION", quartz.getQuartzDescription());
        }
        Page<KQuartz> result = quartzMapper.selectPage(new Page<>(page, rows), queryWrapper);
        return result;
    }
}
