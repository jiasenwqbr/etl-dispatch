package com.jason.datacollection.core.dto;

import lombok.Data;

/**
 * @Description 转换连接线，暂未处理多来源和多去向的情况，后续处理
 * @auther chen1
 * @create 2020-11-29 17:27
 */
@Data
public class HopsDTO {

    private String hopsId;

    //来源ID
    private String fromId;

    //去向ID
    private String toId;

    //是否启用
    private boolean enabled;
}
