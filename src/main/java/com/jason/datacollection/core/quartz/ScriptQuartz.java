package com.jason.datacollection.core.quartz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.jason.datacollection.core.constant.KettleConfig;
import com.jason.datacollection.core.enums.RunResultEnum;
import com.jason.datacollection.core.enums.RunTypeEnum;
import com.jason.datacollection.core.execute.JobExecute;
import com.jason.datacollection.core.execute.TransExecute;
import com.jason.datacollection.core.repository.RepositoryUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jason.datacollection.entity.*;
import com.jason.datacollection.service.*;
import com.jason.datacollection.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.quartz.*;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 作业定时任务执行器
 * 因为定时器的job类和kettle的job类名一样，因此这里采用继承{@code org.quartz。InterruptableJob}类
 *
 * @author lyf
 */
@Slf4j
@DisallowConcurrentExecution
public class ScriptQuartz implements InterruptableJob {

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 此处无法使用常规注入方式注入bean
        KScriptMonitorService monitorService = SpringContextUtil.getBean(KScriptMonitorService.class);
        KScriptService scriptService = SpringContextUtil.getBean(KScriptService.class);
        KScriptRecordService scriptRecordService = SpringContextUtil.getBean(KScriptRecordService.class);
        KQuartzService quartzService = SpringContextUtil.getBean(KQuartzService.class);
        // 本次执行时间
        Date lastExecuteTime = jobExecutionContext.getFireTime();
        // 下一次任务时间
        Date nexExecuteTime = jobExecutionContext.getNextFireTime();
        // 运行状态
        boolean runStatus = true;
        // 获取传入过来的作业ID
        String scriptId = jobExecutionContext.getMergedJobDataMap().getString("id");

        KScript script = scriptService.getById(scriptId);
        // 设置执行参数
        Map<String, String> params = new HashMap<>(2);
        if (StringUtil.hasText(script.getSyncStrategy())) {
            Integer day = Integer.valueOf(script.getSyncStrategy().substring(2, script.getSyncStrategy().length()));

            params.put("start_time", DateUtil.getDateTimeStr(DateUtil.addDays(DateUtil.getTodayStartTime(), -day)));
            params.put("end_time", DateUtil.getDateTimeStr(DateUtil.addDays(DateUtil.getTodayEndTime(), -day)));
        }
        /**
         * 感谢gitee网友booleandev解决job传参问题
         * gitee主页：https://gitee.com/yanjiantao
         */
        // 执行参数加入到 job 中
        String scriptParams = script.getScriptParams();
        //如果没有参数 那么就不put
        if (!StringUtils.isEmpty(scriptParams)) {
            Map jsonToMap = JSON.parseObject(scriptParams);
            params.putAll(jsonToMap);
        }
        // 执行作业并返回日志
        String logText = "";
        String recordId = IdUtil.simpleUUID();
        try {
            // 判断是执行资源库还是执行文件脚本
            switch (RunTypeEnum.getEnum(script.getExecuteType())) {
                case REP:
                    if (script.getScriptType() == "0" || script.getScriptType().equals("0")) {
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
                    if (script.getScriptType() == "0" || script.getScriptType().equals("0")) {
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
            String msg = "执行失败";
            log.error(msg, e);
            logText = e.getMessage();
            //发送邮件
            sendMail(script, logText);
            //webhooks
            sendWebHook(script, logText);
            //失败重试
            failRetry(script, recordId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 执行结束时间
        Date stopDate = new Date();
        // 输出日志到文件中,返回输出路径
        String logPath = writeStringToFile(String.valueOf(scriptId), logText);
        //获取定时任务状态
        KQuartz quartz = quartzService.getById(script.getScriptQuartz());
        //通过下一次执行时间判断是不是一次性任务，如果是一次性任务，执行结束后，修改任务状态
        if (null == nexExecuteTime && null == quartz) {
            script.setScriptStatus("2");
            scriptService.updateById(script);
        }
        // 修改监控表数据
        KScriptMonitor scriptMonitor = new KScriptMonitor();
        scriptMonitor.setMonitorScriptId(scriptId);
        scriptMonitor.setLastExecuteTime(lastExecuteTime);
        scriptMonitor.setNextExecuteTime(nexExecuteTime);
        monitorService.updateMonitor(scriptMonitor, runStatus);

        // 添加作业执行记录
        KScriptRecord scriptRecord = new KScriptRecord();
        scriptRecord.setId(recordId);
        scriptRecord.setLogFilePath(logPath);
        scriptRecord.setRecordStatus(runStatus ? RunResultEnum.SUCCESS.getCode() : RunResultEnum.FAIL.getCode());
        scriptRecord.setRecordScriptId(scriptId);
        scriptRecord.setStartTime(lastExecuteTime);
        scriptRecord.setStopTime(stopDate);
        scriptRecordService.save(scriptRecord);
    }

    /**
     * 输出日志到文件
     *
     * @param jobId   作业ID
     * @param logText 日志内容
     * @return 日志输出路径
     */
    private String writeStringToFile(String jobId, String logText) {
        String logPath = KettleConfig.logFilePath.concat("/").concat("job/").concat(jobId).concat("/").concat(String.valueOf(System.currentTimeMillis())).concat(".txt");
        try {
            FileUtil.writeStringToFile(new File(logPath), logText, KettleConfig.encoding.name(), false);
        } catch (IOException e) {
            String msg = "输出日志到文件失败";
            log.error(msg, e);
        }
        return logPath;
    }

    /***
     * 发送邮件
     * @param script
     */
    public void sendMail(KScript script, String logText) {
        MailUtils mailUtils = SpringContextUtil.getBean(MailUtils.class);
        if (mailUtils.isStart()) {
            MailAccount account = mailUtils.createSendConfig();
            String mailMessage = "采集任务执行失败，任务名：" + script.getScriptName() + ",错误信息：" + logText + ",失败时间：" + DateUtil.currentDateTimeStr();
            log.info("邮件收件人：" + mailUtils.getRecipient() + "\n邮件内容：" + mailMessage);
            String send = MailUtil.send(account, CollUtil.newArrayList(mailUtils.getRecipient()), "数据治理平台-任务失败通知", mailMessage, false);
            log.info(send);
        }
    }

    /**
     * 调用webhooks
     *
     * @param script
     */
    public void sendWebHook(KScript script, String logText) {
        WebHooks webHooks = SpringContextUtil.getBean(WebHooks.class);
        if (webHooks.isStart()) {
            JSONObject text = new JSONObject();
            JSONObject param = new JSONObject();
            //消息内容
            text.put("content", "采集任务：" + script.getScriptName() + "，执行失败\n" + logText + "\n失败时间" + DateUtil.currentDateTimeStr());
            param.put("msgtype", "text");
            param.put("text", text);
            if (StringUtils.isNotEmpty(webHooks.getDingtalk())) {
                HttpRequest request = HttpUtil.
                        createPost(webHooks.getDingtalk()).
                        contentType(MediaType.APPLICATION_JSON_VALUE).
                        body(param.toJSONString());
                request.execute();
            }
            if (StringUtils.isNotEmpty(webHooks.getWechat())) {
                HttpRequest request = HttpUtil.
                        createPost(webHooks.getWechat()).
                        contentType(MediaType.APPLICATION_JSON_VALUE).
                        body(param.toJSONString());
                request.execute();
            }
        }
    }

    /**
     * 失败重试
     *
     * @param script
     * @param recordId
     * @throws Exception
     */
    public void failRetry(KScript script, String recordId) throws Exception {
        KFailRetryLogService kFailRetryLogService = SpringContextUtil.getBean(KFailRetryLogService.class);
        String failLogText = "";
        //判断是否有失败重试
        if ("1".equals(script.getFailRetryType())) {

        } else if ("2".equals(script.getFailRetryType())) {
            if (script.getFailRetryScriptId().startsWith("job")) {
                failLogText = JobExecute.run(RepositoryUtil.getAbstractRepository(script.getScriptRepositoryId())
                        , script.getFailRetryScriptPath(), script.getFailRetryScriptName()
                        , null, (Map) JSON.parse(script.getFailRetryParams())
                        , LogLevel.getLogLevelForCode(script.getScriptLogLevel()));
            } else {
                failLogText = TransExecute.run(RepositoryUtil.getAbstractRepository(script.getScriptRepositoryId())
                        , script.getFailRetryScriptPath(), script.getFailRetryScriptName()
                        , null, (Map) JSON.parse(script.getFailRetryParams())
                        , LogLevel.getLogLevelForCode(script.getScriptLogLevel()));
            }
        }
        KFailRetryLog kFailRetryLog = new KFailRetryLog();
        kFailRetryLog.setId(IdUtil.simpleUUID());
        kFailRetryLog.setExecuteTime(new Date());
        kFailRetryLog.setFailRetryScriptId(script.getId());
        kFailRetryLog.setFailRetryRecordId(recordId);
        kFailRetryLog.setFailRetryLog(failLogText);
        kFailRetryLogService.save(kFailRetryLog);
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }
}
