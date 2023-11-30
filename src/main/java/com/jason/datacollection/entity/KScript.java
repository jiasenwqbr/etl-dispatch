package com.jason.datacollection.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class KScript {

    private String id;
    private String categoryId;
    @TableField(exist = false)
    private String categoryName;
    private String scriptName;
    private String scriptDescription;
    private String scriptType;
    private String executeType;
    private String scriptPath;
    private String scriptRepositoryId;
    private String scriptQuartz;
    @TableField(exist = false)
    private String quartzDescription;
    private String syncStrategy;
    private String scriptLogLevel;
    private String scriptStatus;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;
    private String addUser;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date editTime;
    private String editUser;
    private String delFlag;
    private String scriptParams;

    private int failRetryTime;

    private String failRetryScriptPath;

    private String failRetryScriptName;

    private String failRetryScriptId;

    private String failRetryParams;

    private String failRetryType;
    @TableField(exist = false)
    //虚拟字段，仅用于首页统计
    private Integer num;
}
