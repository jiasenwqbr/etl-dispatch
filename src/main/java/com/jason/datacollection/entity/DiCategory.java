package com.jason.datacollection.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 整合分类表
 *
 * @author z
 */
@EqualsAndHashCode()
@Data
public class DiCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String code;

    private String categoryPid;

    private String isDefault;

    private String repId;

    private String path;

    private String categoryId;

    private List<DiCategory> children;

}
