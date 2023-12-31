package com.jason.datacollection.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class KFailRetryLog {

    private String id;
    private String failRetryScriptId;
    private String failRetryRecordId;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;
    private String failRetryLog;

}
