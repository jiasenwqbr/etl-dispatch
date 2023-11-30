package com.jason.datacollection.service.impl;

import com.jason.datacollection.core.povo.TreeDTO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.core.repository.RepositoryUtil;
import com.jason.datacollection.mapper.DiCategoryMapper;
import com.jason.datacollection.mapper.DiScriptMapper;
import com.jason.datacollection.mapper.KRepositoryMapper;
import com.jason.datacollection.service.DiScriptService;
import com.jason.datacollection.entity.DiCanstant;
import com.jason.datacollection.entity.DiCategory;
import com.jason.datacollection.entity.DiScript;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.*;
import org.pentaho.di.trans.TransMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DiScriptServiceImpl extends ServiceImpl<DiScriptMapper, DiScript> implements DiScriptService {
    @Autowired
    DiCategoryMapper diCategoryMapper;
    @Autowired
    DiScriptMapper diScriptMapper;
    @Autowired
    KRepositoryMapper kRepositoryMapper;

    /**
     * 获取某个目录下的所有子目录Id
     */
    public static void getChildCategoryId(DiCategory d, List<DiCategory> ds, List<String> s) {
        if (Objects.nonNull(ds)) {
            ds.forEach(a -> {
                if (d.getCategoryId().equals(a.getCategoryPid())) {
                    s.add(a.getCategoryId());
                    getChildCategoryId(a, ds, s);
                }
            });
        }
    }

    public Result add(JSONObject json) throws Exception {
        Result result = new Result();
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(json.getString("repId"));
        RepositoryDirectoryInterface directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(json.getLong("dirId")));
        ObjectId id;
        if (DiCanstant.TRANS.equals(json.getString("type"))) {
            id = abstractRepository.getTransformationID(json.getString("name"), directory);
            if (id == null) {
                TransMeta a = new TransMeta();
                a.setRepositoryDirectory(directory);
                a.setName(json.getString("name"));
                abstractRepository.save(a, json.getString("name"), null);
                id = abstractRepository.getTransformationID(json.getString("name"), directory);
            } else {
                result.setCode("9999");
                result.setMessage("当前转换已存在！");
            }
        } else {
            id = abstractRepository.getJobId(json.getString("name"), directory);
            if (id == null) {
                JobMeta jobMeta = new JobMeta();
                jobMeta.setRepositoryDirectory(directory);
                jobMeta.setName(json.getString("name"));
                abstractRepository.save(jobMeta, json.getString("name"), null);
                id = abstractRepository.getJobId(json.getString("name"), directory);
            } else {
                result.setCode("9999");
                result.setMessage("当前作业已存在！");
            }
        }
        result.setResult(id);
        return result;
    }

    public void delete(String repId, String id, String type) throws Exception {
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(repId);
        //RepositoryDirectoryInterface directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(json.getLong("dirId")));
        if (DiCanstant.TRANS.equals(type)) {
            abstractRepository.deleteTransformation(new LongObjectId(Long.valueOf(id)));
        } else {
            abstractRepository.deleteJob(new LongObjectId(Long.valueOf(id)));
        }
    }

    public void update(JSONObject json) throws Exception {
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(json.getString("repId"));
        RepositoryDirectoryInterface directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(json.getLong("dirId")));
        if (DiCanstant.TRANS.equals(json.getString("type"))) {
            abstractRepository.renameTransformation(new LongObjectId(json.getLong("scriptId")), directory, json.getString("name"));
        } else {
            abstractRepository.renameJob(new LongObjectId(json.getLong("scriptId")), directory, json.getString("name"));
        }
    }

    public Result<List<TreeDTO<String>>> findDiScriptByCatagoryId(String id, String repId, Integer pageNum, Integer pageSize) {
        List<TreeDTO<String>> subdirectories = null;
        try {
            AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(repId);
            RepositoryDirectoryInterface directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(Long.valueOf(id).longValue()));
            subdirectories = RepositoryUtil.getElementTree(abstractRepository, directory, null);
            Result.ok(subdirectories, subdirectories.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(subdirectories, subdirectories.size());
    }

    public Result moveScript(JSONObject json) throws Exception {
        Result result = new Result();
        result.setCode("0000");
        result.setMessage("移动成功。");
        AtomicReference<Boolean> isMove = new AtomicReference<>(true);
        AbstractRepository abstractRepository = RepositoryUtil.getAbstractRepository(json.getString("repId"));
        RepositoryDirectoryInterface directory = abstractRepository.loadRepositoryDirectoryTree().findDirectory(new LongObjectId(json.getLong("dirId")));
        LongObjectId scriptId = new LongObjectId(Long.valueOf(json.getLong("scriptId")));
        //判断目标目录是否有重名
        List<RepositoryElementMetaInterface> jobAndTransformationObjects = abstractRepository.getJobAndTransformationObjects(directory.getObjectId(), false);
        if (DiCanstant.TRANS.equals(json.getString("type"))) {
            TransMeta transMeta = abstractRepository.loadTransformation(scriptId, null);
            jobAndTransformationObjects.forEach(meta -> {
                if (meta.getName().equals(transMeta.getName())) {
                    result.setCode("9999");
                    result.setMessage("移动失败，目标目录存在同名转换！");
                    isMove.set(false);
                }
            });
            if (isMove.get()) {
                abstractRepository.renameTransformation(scriptId, directory, null);
            }
        } else {
            JobMeta jobMeta = abstractRepository.loadJob(scriptId, null);
            jobAndTransformationObjects.forEach(meta -> {
                if (meta.getName().equals(jobMeta.getName())) {
                    result.setCode("9999");
                    result.setMessage("移动失败，目标目录存在同名作业！");
                    isMove.set(false);
                }
            });
            if (isMove.get()) {
                abstractRepository.renameJob(new LongObjectId(Long.valueOf(json.getLong("scriptId"))), directory, null);
            }
        }
        return result;
    }

    public Map<String, Integer> findTransAndJobById(String id) {
        Map<String, Integer> map = new HashMap<>(8);
        List<DiScript> trans = diScriptMapper.findByRepIdAndType(id, DiCanstant.TRANS);
        List<DiScript> jobs = diScriptMapper.findByRepIdAndType(id, DiCanstant.JOB);
        map.put("trans", trans.size());
        map.put("jobs", jobs.size());
        List<DiCategory> byIsDefault = diCategoryMapper.findByIsDefaultAndRepId(DiCanstant.IS_DEFAULT, id);
        List<DiCategory> all = diCategoryMapper.selectByRepId(id);
        List<String> cjList = new ArrayList<>();
        List<String> aqList = new ArrayList<>();
        List<String> jmList = new ArrayList<>();
        List<String> gxList = new ArrayList<>();
        byIsDefault.forEach(b -> {
            switch (b.getName()) {
                case "数据采集":
                    cjList.add(b.getCategoryId());
                    getChildCategoryId(b, all, cjList);
                    break;
                case "数据安全":
                    aqList.add(b.getCategoryId());
                    getChildCategoryId(b, all, aqList);
                    break;
                case "数据建模":
                    jmList.add(b.getCategoryId());
                    getChildCategoryId(b, all, jmList);
                    break;
                case "数据共享":
                    gxList.add(b.getCategoryId());
                    getChildCategoryId(b, all, gxList);
                    break;
                default:
                    break;
            }
        });
        if (Objects.nonNull(cjList)) {
            map.put("cjtrans", diScriptMapper.findByCategoryIdInAndRepIdAndType(cjList, id, DiCanstant.TRANS).size());
            map.put("cjjobs", diScriptMapper.findByCategoryIdInAndRepIdAndType(cjList, id, DiCanstant.JOB).size());
        } else {
            map.put("cjtrans", 0);
            map.put("cjjobs", 0);
        }
        if (Objects.nonNull(aqList)) {
            map.put("aqtrans", diScriptMapper.findByCategoryIdInAndRepIdAndType(aqList, id, DiCanstant.TRANS).size());
            map.put("aqjobs", diScriptMapper.findByCategoryIdInAndRepIdAndType(aqList, id, DiCanstant.JOB).size());
        } else {
            map.put("aqtrans", 0);
            map.put("aqjobs", 0);
        }
        if (Objects.nonNull(gxList)) {
            map.put("gxtrans", diScriptMapper.findByCategoryIdInAndRepIdAndType(gxList, id, DiCanstant.TRANS).size());
            map.put("gxjobs", diScriptMapper.findByCategoryIdInAndRepIdAndType(gxList, id, DiCanstant.JOB).size());
        } else {
            map.put("gxtrans", 0);
            map.put("gxjobs", 0);
        }
        if (Objects.nonNull(jmList)) {
            map.put("jmtrans", diScriptMapper.findByCategoryIdInAndRepIdAndType(jmList, id, DiCanstant.TRANS).size());
            map.put("jmjobs", diScriptMapper.findByCategoryIdInAndRepIdAndType(jmList, id, DiCanstant.JOB).size());
        } else {
            map.put("jmtrans", 0);
            map.put("jmjobs", 0);
        }
        return map;
    }
}

