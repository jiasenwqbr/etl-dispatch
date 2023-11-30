package com.jason.datacollection.entity;

import lombok.Data;

@Data
public class DiRespository {

    /**
     * 资源库ID
     */
    private Integer id;

    /**
     * 资源库名称
     */
    private String repName;

    /**
     * 资源库用户名
     */
    private String repUsername;

    /**
     * 资源库密码
     */
    private String repPassword;

    /**
     * 资源库url
     */
    private String url;

    /**
     * 资源库转换id
     */
    private String transId;

}
