package com.jason.datacollection.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.core.constant.KettleConfig;
import com.jason.datacollection.core.dto.QuartzDTO;
import com.jason.datacollection.core.enums.GlobalStatusEnum;
import com.jason.datacollection.core.enums.RunStatusEnum;
import com.jason.datacollection.core.enums.RunTypeEnum;
import com.jason.datacollection.core.exceptions.MyMessageException;
import com.jason.datacollection.core.execute.JobExecute;
import com.jason.datacollection.core.execute.TransExecute;
import com.jason.datacollection.core.quartz.QuartzManage;
import com.jason.datacollection.core.quartz.ScriptQuartz;
import com.jason.datacollection.core.repository.RepositoryUtil;
import com.jason.datacollection.entity.KQuartz;
import com.jason.datacollection.entity.KRepository;
import com.jason.datacollection.entity.KScript;
import com.jason.datacollection.entity.KScriptMonitor;
import com.jason.datacollection.mapper.KQuartzMapper;
import com.jason.datacollection.mapper.KRepositoryMapper;
import com.jason.datacollection.mapper.KScriptMapper;
import com.jason.datacollection.mapper.KScriptMonitorMapper;
import com.jason.datacollection.service.KScriptService;
import com.jason.datacollection.util.DateUtil;
import com.jason.datacollection.util.FileUtil;
import com.jason.datacollection.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//import static cn.youth.datacollection.init.RepInit.repMap;

@Service
@Slf4j
public class KScriptServiceImpl extends ServiceImpl<KScriptMapper, KScript> implements KScriptService {

    @Autowired
    KScriptMapper scriptMapper;

    @Autowired
    KQuartzMapper quartzMapper;

    @Autowired
    KRepositoryMapper repositoryMapper;
    @Autowired
    KScriptMonitorMapper kScriptMonitorMapper;

    /*private static void accept(KScript script) {
        startScript(script.getId());
    }*/

    @Override
    public PageInfo<KScript> findScriptListByPage(KScript script, Integer page, Integer rows) {
//        QueryWrapper<KScript> queryWrapper = new QueryWrapper<>();
//        if (script != null) {
//            queryWrapper.orderByDesc("edit_time");
//            if (script.getScriptName()!=null) {
//                queryWrapper.like("script_name", script.getScriptName());
//            }
//            if (script.getCategoryId()!=null&&!script.getCategoryId().equals("")) {
//                queryWrapper.eq("CATEGORY_ID", script.getCategoryId());
//            }
//        }
//        Page<KScript> result = scriptMapper.selectPage(new Page<KScript>(page, rows), queryWrapper);
        if (script.getCategoryId().equals("")) {
            script.setCategoryId(null);
        }
        PageHelper.startPage(page, rows);
        List<KScript> kScripts = scriptMapper.selectAll(script);
        PageInfo<KScript> pageInfo = new PageInfo<>(kScripts);
        return pageInfo;
//        return result;
    }

    //查询缓存中是否有脚本
    /*public String checkCatch(String repId, String tjId, String type, JSONObject para, Integer isStart) throws KettleException {
        String index = "repj/";
        if (type == "1") {
            index = "rept/";
        }
        List<Integer> tree = repMap.get(index + repId);
        if (tree != null && tree.contains(tjId)) {
            addKettleTaskD(repId, tjId.toString(), type, para, isStart);
            if (isStart == 1) {
                return "配置成功，正在执行";
            }
            return "配置成功";
        }
        return "无此资源库";
    }*/

    /**
     * 因程序中断后所有的定时会中断，因此在程序启动的时候需要初始化类调用该方法重新恢复定时任务
     */
    public void initScriptQuartz() {
        QueryWrapper<KScript> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("SCRIPT_STATUS", RunStatusEnum.RUN.getCode());
        List<KScript> scriptList = scriptMapper.selectList(queryWrapper);
        List<KQuartz> quartzList = quartzMapper.selectBatchIds(scriptList.stream().map(KScript::getScriptQuartz).distinct().collect(Collectors.toList()));
        List<KQuartz> quarts = quartzList.stream().filter(quartz -> StringUtil.hasText(quartz.getQuartzCron())).collect(Collectors.toList());
        List<KScript> collect = scriptList.stream().filter(script -> quarts.stream().anyMatch(quartz -> quartz.getId().equals(script.getScriptQuartz()))).collect(Collectors.toList());
        collect.forEach(script -> stopScript(script.getId()));
    }

    /**
     * 启动所有任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void startAllScript() {
//        QueryWrapper<KScript> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("script_status", RunStatusEnum.STOP.getCode());
        KScript kScript = new KScript();
        kScript.setScriptStatus(RunStatusEnum.STOP.getCode());
        List<KScript> scriptList = scriptMapper.selectList(new QueryWrapper<>());
        scriptList.forEach((script) -> {
            startScript(script.getId(), false);
        });
    }

    /**
     * 根据ID启动任务
     *
     * @param id
     * @param executeOnce
     */
    @Transactional(rollbackFor = Exception.class)
    public void startScript(String id, boolean executeOnce) {
        String cron = "";
        // 查询转换信息
        KScript script = scriptMapper.selectById(id);
        if (script == null) {
            throw new MyMessageException("当前脚本不存在");
        }
        if (!executeOnce) {
            // 查询定时策略
            KQuartz kQuartz = quartzMapper.selectById(script.getScriptQuartz());
            if (kQuartz == null) {
                throw new MyMessageException("当前定时策略不存在");
            }
            cron = kQuartz.getQuartzCron();
        }
        // 修改监控状态
        updateScriptMonitorStatus(script.getId(), RunStatusEnum.RUN);
        // 修改转换状态
        script.setScriptStatus(RunStatusEnum.RUN.getCode());
        scriptMapper.updateById(script);
        // 获取定时任务需要的参数
        QuartzDTO quartzDTO = getQuartzDTO(script, cron);
        // 根据定时策略添加任务
        if (StringUtil.hasText(cron)) {
            // 添加定时任务
            QuartzManage.addCronJob(quartzDTO);
        } else {
            // 添加一次性任务
            QuartzManage.addOnceJob(quartzDTO, executeOnce);
        }
    }

    public void startCollectionTask(KScript kScript) {
        // 查询定时策略
        KQuartz kQuartz = quartzMapper.selectById(kScript.getScriptQuartz());
        if (kQuartz == null) {
            throw new MyMessageException("当前定时策略不存在");
        }
        // 获取定时任务需要的参数
        QuartzDTO quartzDTO = getQuartzDTO(kScript, kQuartz.getQuartzCron());
        // 根据定时策略添加任务
        if (StringUtil.hasText(kQuartz.getQuartzCron())) {
            // 添加定时任务
            QuartzManage.addCronJob(quartzDTO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void stopAllScript() {
        KScript kScript = new KScript();
        kScript.setScriptStatus(RunStatusEnum.RUN.getCode());
        List<KScript> scriptList = scriptMapper.selectList(new QueryWrapper<>());
        scriptList.forEach((script) -> {
            stopScript(script.getId());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void stopScript(String id) {
        // 查询作业信息
        KScript script = scriptMapper.selectById(id);
        if (script == null) {
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, "当前作业不存在");
        }
        // 已经关闭的任务不在处理
        if (RunStatusEnum.STOP.getCode().equals(script.getScriptStatus())) {
            return;
        }

        // 修改监控状态
        updateScriptMonitorStatus(script.getId(), RunStatusEnum.STOP);

        // 修改作业状态
        script.setScriptStatus(RunStatusEnum.STOP.getCode());
        scriptMapper.updateById(script);

        // 关闭定时任务
        QuartzManage.removeJob(getQuartzDTO(script, null));
    }

    @Override
    public KScript getById(String id) {
        return scriptMapper.selectById(id);
    }

    @Override
    public List<KScript> taskCount() {
        return scriptMapper.taskCount();
    }

    /**
     * 修改监控信息状态
     *
     * @param scriptId   脚本ID
     * @param statusEnum 状态枚举
     */
    private void updateScriptMonitorStatus(String scriptId, RunStatusEnum statusEnum) {
        KScriptMonitor kScriptMonitor = kScriptMonitorMapper.selectBySid(scriptId);
        if (kScriptMonitor == null) {
            kScriptMonitor = new KScriptMonitor();
            kScriptMonitor.setMonitorFail(0);
            kScriptMonitor.setMonitorSuccess(0);
            kScriptMonitor.setMonitorScriptId(scriptId);
            kScriptMonitor.setRunStatus(System.currentTimeMillis() + "-");
            kScriptMonitor.setMonitorStatus(statusEnum.getCode());
            kScriptMonitorMapper.insert(kScriptMonitor);
        } else {
            switch (statusEnum) {
                case RUN:
                    String runStatus = kScriptMonitor.getRunStatus();
                    if (runStatus.endsWith("-")) {
                        runStatus = runStatus.concat(String.valueOf(System.currentTimeMillis()));
                    }
                    kScriptMonitor.setRunStatus(runStatus.concat(",").concat(System.currentTimeMillis() + "-"));
                    break;
                case STOP:
                    kScriptMonitor.setRunStatus(kScriptMonitor.getRunStatus().concat(String.valueOf(System.currentTimeMillis())));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + statusEnum);
            }
            kScriptMonitor.setMonitorStatus(statusEnum.getCode());
            kScriptMonitorMapper.updateById(kScriptMonitor);
        }
    }

    /**
     * 根据定时策略和作业组装执行参数
     *
     * @param script 作业信息
     * @param cron   定时策略
     * @return {@link QuartzDTO}
     */
    private QuartzDTO getQuartzDTO(KScript script, String cron) {
        String categoryId = script.getCategoryId() == null ? "-" : String.valueOf(script.getCategoryId());
        QuartzDTO dto = new QuartzDTO();
        String scriptType = "JOB";
        if (script.getScriptType() == "1") {
            scriptType = "TRANS";
        }
        dto.setJobName(scriptType + "@" + script.getId());
        dto.setJobGroupName(scriptType + "_GROUP@" + categoryId + "@" + script.getId());
        dto.setTriggerName(scriptType + "_TRIGGER@" + script.getId());
        dto.setTriggerGroupName(scriptType + "_TRIGGER_GROUP@" + categoryId + "@" + script.getId());
        if (StringUtil.hasText(cron)) {
            dto.setCron(cron);
        }
        dto.setJobClass(ScriptQuartz.class);
        dto.setJobDataMap(new JobDataMap(ImmutableMap.of("id", script.getId())));
        return dto;
    }

    /**
     * @param repId
     * @param tjId
     * @param type    type=0为作业，type=1为转换
     * @param para
     * @param isStart
     * @throws KettleException
     */
    @Async
    public void addKettleTaskD(String repId, String tjId, String type, JSONObject para, Integer isStart) throws Exception {
        KRepository kRepository = repositoryMapper.selectById(repId);
        QueryWrapper<KScript> queryWrapper = new QueryWrapper<>();
        //转换/作业是否存在
        boolean isExit = false;
        //转换/作业 文件路径
        String path = null;
        String scriptName = null;
        if (kRepository != null) {
            // 连接资源库
            AbstractRepository repository = RepositoryUtil.getAbstractRepository(repId);
            if (type == "1") {
                //开始处理转换
                ObjectId id = new StringObjectId(tjId);
                TransMeta transMeta = repository.loadTransformation(id, "");
                isExit = transMeta == null ? false : true;
                path = transMeta.getPathAndName();
                scriptName = transMeta.getName();
            } else {
                ObjectId id = new StringObjectId(tjId);
                JobMeta jobMeta = repository.loadJob(id, "");
                isExit = jobMeta == null ? false : true;
                path = jobMeta.getRepositoryDirectory().getPath().concat("/").concat(jobMeta.getName());
                scriptName = jobMeta.getName();
            }

            if (isExit) {
                log.info("没有查到此脚本");
            } else {
                queryWrapper.eq("SCRIPT_PATH", path);
                queryWrapper.eq("SCRIPT_PARAMS", para.toJSONString().trim());
                queryWrapper.eq("SCRIPT_REPOSITORY_ID", repId);
                KScript script = scriptMapper.selectOne(queryWrapper);
                String scriptId = script.getId();
                //判断脚本信息是否入库，未入库则先入库，后启动
                if (script == null) {
                    scriptId = StringUtil.uuid();
                    scriptMapper.insert(createScript(scriptId, path, scriptName, String.valueOf(repId), para.toJSONString().trim(), String.valueOf(type)));
                }
                if (isStart == 1) {
                    startScript(scriptId, false);
                }
            }
        }
    }

    @Async
    public void startOne(String scriptId, String uuid) {
    /*    TableMonitor tableMonitor = new TableMonitor();
        tableMonitor.setId(uuid);
        tableMonitor.setRunStatus("运行中");
        tableMonitor.setTransid(transId);
        tablesMappers.addTm(tableMonitor);*/
        Map re = new HashMap();
        boolean runStatus = true;
        // 获取转换
        KScript script = scriptMapper.selectById(scriptId);
        // 设置执行参数
        Map<String, String> params = new HashMap<>(2);
        String scriptParams = script.getScriptParams();
        Map jsonToMap = JSON.parseObject(scriptParams);
        params.putAll(jsonToMap);
        if (StringUtil.hasText(script.getSyncStrategy())) {
            Integer day = Integer.valueOf(script.getSyncStrategy().substring(2, script.getSyncStrategy().length()));
            params.put("start_time", DateUtil.getDateTimeStr(DateUtil.addDays(DateUtil.getTodayStartTime(), -day)));
            params.put("end_time", DateUtil.getDateTimeStr(DateUtil.addDays(DateUtil.getTodayEndTime(), -day)));
        }
        // 执行转换并返回日志
        String logText = "";
        try {
            Thread.currentThread().sleep(10000);
            // 判断是执行资源库还是执行文件脚本
            switch (RunTypeEnum.getEnum(script.getExecuteType())) {
                case REP:
                    if (script.getScriptType() == "0") {
                        logText = JobExecute.run(RepositoryUtil.getAbstractRepository(script.getScriptRepositoryId())
                                , script.getScriptPath(), script.getScriptName()
                                , null, params
                                , LogLevel.getLogLevelForCode(script.getScriptLogLevel()));
                    } else {
                        logText = TransExecute.run(RepositoryUtil.getAbstractRepository(script.getScriptRepositoryId())
                                , script.getScriptPath(), script.getScriptName()
                                , null, params
                                , LogLevel.getLogLevelForCode(script.getScriptLogLevel()));
                    }
                    break;
                case FILE:
                    if (script.getScriptType() == "0") {
                        logText = JobExecute.run(script.getScriptPath(), null
                                , LogLevel.getLogLevelForCode(script.getScriptLogLevel()));
                    } else {
                        logText = TransExecute.run(script.getScriptPath(), null
                                , LogLevel.getLogLevelForCode(script.getScriptLogLevel()));
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + RunTypeEnum.getEnum(script.getExecuteType()));
            }
        } catch (KettleException e) {
            runStatus = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 执行结束时间
        Date stopDate = new Date();
        // 输出日志到文件中,返回输出路径
        String logPath = writeStringToFile(String.valueOf(scriptId), logText);
/*        if (runStatus) {
            tableMonitor.setRunStatus("执行成功");
        } else {
            tableMonitor.setRunStatus("执行失败");
        }
        tableMonitor.setLogPath(logPath);
        tablesMappers.updateTm(tableMonitor);*/
    }


    /**
     * 输出日志到文件
     *
     * @param transId 转换ID
     * @param logText 日志内容
     * @return 日志输出路径
     */
    private String writeStringToFile(String transId, String logText) {
        String logPath = KettleConfig.logFilePath.concat("/").concat("trans/").concat(transId).concat("/").concat(String.valueOf(System.currentTimeMillis())).concat(".txt");
        try {
            FileUtil.writeStringToFile(new File(logPath), logText, KettleConfig.encoding.name(), false);
        } catch (IOException e) {
            String msg = "输出日志到文件失败";
            log.error(msg, e);
        }
        return logPath;
    }

    /**
     * 创建脚本对象
     *
     * @param scriptId   脚本ID
     * @param scriptPath 脚本路径
     * @param ScriptName 脚本名称
     * @param repId      资源库ID
     * @param param      参数（JSON格式）
     * @param scriptType 脚本类型（0：job;1：trans)
     * @return
     */
    KScript createScript(String scriptId, String scriptPath, String ScriptName, String repId, String param, String scriptType) {
        KScript script = new KScript();
        script.setId(scriptId);
        script.setScriptPath(scriptPath);
        script.setScriptRepositoryId(repId);
        script.setScriptName(ScriptName);
        script.setScriptParams(param);
        script.setScriptQuartz("1");
        script.setScriptLogLevel("Basic");
        script.setExecuteType("rep");
        script.setScriptType(scriptType);
        script.setScriptStatus(RunStatusEnum.STOP.getCode());
        return script;
    }
}
