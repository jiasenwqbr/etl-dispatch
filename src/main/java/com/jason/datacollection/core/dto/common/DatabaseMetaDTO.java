package com.jason.datacollection.core.dto.common;

import lombok.Data;

/**
 * 数据库实体类
 *
 * @author chenzhao
 */
@Data
public class DatabaseMetaDTO {

    //数据库名称
    private String databaseName;

    //端口号
    private String databasePortNumberString;

    //连接名车才能够
    private String displayName;

    //驱动
    private String driverClass;

    //主机名称
    private String hostname;

    //连接名称
    private String name;

    //连接ID
    private String objectId;

    //用户名
    private String username;

    //密码
    private String password;

    //数据库插件ID
    private String pluginId;

}
