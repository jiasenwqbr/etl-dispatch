package com.jason.datacollection.core.dto;


import lombok.Data;

import java.util.Date;

/**
 * 转换步骤实体类
 *
 * @author chenzhao
 */
@Data
public class TransStepDTO {

    //转换ID
    private String stepId;

    //步骤名称
    private String stepName;

    //步骤类型，后续替换为枚举类
    private String stepType;

    //步骤属性
    private StepInterface stepInterface;

    //步骤变更日期
    private Date stepChangedDate;

    //X轴位置
    private int locationX;

    //Y轴位置
    private int locationY;
}
