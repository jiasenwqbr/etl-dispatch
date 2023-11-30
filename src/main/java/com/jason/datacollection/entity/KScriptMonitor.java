package com.jason.datacollection.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class KScriptMonitor {

    private String id;
    @TableField(exist = false)
    private String scriptName;
    @TableField(exist = false)
    private String categoryId;
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String scriptDescription;
    private String monitorScriptId;
    private Integer monitorSuccess;
    private Integer monitorFail;
    private String monitorStatus;
    private String runStatus;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastExecuteTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextExecuteTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;
    private String addUser;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date editTime;
    private String editUser;
    private String delFlag;

}
