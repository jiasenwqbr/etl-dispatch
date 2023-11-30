package com.jason.datacollection.core.dto;


import lombok.Data;

import java.util.List;

/**
 * 转换实体类
 *
 * @author chenzhao
 */
@Data
public class TransDTO {

    //转换名称
    private String transName;

    //步骤列表
    private List<TransStepDTO> stepList;



}
