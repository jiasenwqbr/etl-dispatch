package com.jason.datacollection.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jason.datacollection.core.povo.TreeDTO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.core.repository.RepositoryUtil;
import com.jason.datacollection.mapper.DiCategoryMapper;
import com.jason.datacollection.mapper.DiScriptMapper;
import com.jason.datacollection.mapper.KRepositoryMapper;
import com.jason.datacollection.service.DiCategoryService;
import com.jason.datacollection.util.BeanUtil;
import com.jason.datacollection.entity.DiCategory;
import com.jason.datacollection.entity.DiRespository;
import com.jason.datacollection.entity.KRepository;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiCategoryServiceImpl extends ServiceImpl<DiCategoryMapper, DiCategory> implements DiCategoryService {
    @Autowired
    DiCategoryMapper diCategoryMapper;
    @Autowired
    DiScriptMapper diScriptMapper;
    @Autowired
    KRepositoryMapper kRepositoryMapper;
    @Value("${spoon.url}")
    private String REPOSITORY_URL;

    @Override
    public Result<Object> delete(String dirId, String repId) throws Exception {
        Result result = new Result();
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(repId);
        RepositoryDirectoryInterface directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(Long.valueOf(dirId)));
        List<RepositoryDirectoryInterface> children = directory.getChildren();
        if (children.size() == 0) {
            abstractRepository.deleteRepositoryDirectory(directory);
            result.setCode("0000");
            result.setMessage("删除成功！");
        } else {
            result.setCode("9999");
            result.setMessage("当前目录存在下级目录或脚本，不允许删除！");
        }
        return result;
    }

    public void update(JSONObject json) throws Exception {
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(json.getString("repId"));
        RepositoryDirectoryInterface directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(json.getLong("dirId")));
        abstractRepository.renameRepositoryDirectory(new LongObjectId(json.getLong("dirId")), directory.getParent(), json.getString("name"));
    }

    public List<TreeDTO<String>> findDiCateryByRep(String repId) throws Exception {
        List<TreeDTO<String>> treeList = new ArrayList<>();
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(repId);
        // 获取当前的路径信息
        RepositoryDirectoryInterface rdi = abstractRepository.loadRepositoryDirectoryTree().findDirectory("/");
        List<TreeDTO<String>> subdirectories = RepositoryUtil.getSubdirectories(rdi);
        subdirectories.forEach(dt -> {
            try {
                treeList.addAll(RepositoryUtil.getSubdirectories(abstractRepository.loadRepositoryDirectoryTree().findDirectory(dt.getExtra())));
            } catch (KettleException e) {
                e.printStackTrace();
            }
        });
        treeList.addAll(subdirectories);
        //List<DiCategory> all = diCategoryMapper.findByRepIdOrderByCode(repId);
        return treeList;
    }

    public DiRespository findRepositoryById(String id) {
        KRepository kRepository = kRepositoryMapper.selectById(id);
        DiRespository repDto = null;
        if (kRepository != null) {
            repDto = BeanUtil.copyProperties(kRepository, DiRespository.class);
        }
        repDto.setUrl(REPOSITORY_URL);
        return repDto;
    }

    public Result add(JSONObject json) throws Exception {
        Result result = new Result();
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(json.getString("repId"));
        RepositoryDirectoryInterface directory;
        if (json.containsKey("dirId")&&!StrUtil.isEmpty(json.getString("dirId"))) {
            directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(json.getLong("dirId")));
        } else {
            directory = abstractRepository.getUserHomeDirectory();
        }
        RepositoryDirectoryInterface directoryChild = directory.findChild(json.getString("name"));
        if (directoryChild == null) {
            abstractRepository.createRepositoryDirectory(directory, json.getString("name"));
            result.setCode("0000");
            result.setMessage("创建成功！");
        } else {
            result.setCode("9999");
            result.setMessage("目录已存在！");
        }

        return result;
    }
}
