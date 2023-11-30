package com.jason.datacollection.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 整合分类表
 *
 * @author z
 */
@EqualsAndHashCode()
@Data
public class DiScript implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String type;

    private String name;

    private String path;

    // 数据整合分类id
    private String categoryId;

    // 资源库脚本(转换或任务)id
    private String scriptId;

    // 资源库id
    private String repId;

    @ApiModelProperty(value = "创建时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;

}
