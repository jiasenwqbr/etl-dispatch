package com.jason.datacollection.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class KScriptRecord {

    private String id;
    private String recordScriptId;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date stopTime;
    private String recordStatus;
    private String logFilePath;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;
    private String addUser;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date editTime;
    private String editUser;
    private String delFlag;
    private String categoryId;

    @TableField(exist = false)
    private String scriptName;

    @TableField(exist = false)
    private String scriptDescription;
    @TableField(exist = false)
    private String categoryName;


}
