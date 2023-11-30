package com.jason.datacollection.core.repository;

import com.jason.datacollection.entity.KRepository;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;

import java.util.Properties;

/**
 * @ClassName DataBaseRepository
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/9 17:10
 **/
@Slf4j
public class DataBaseRepository implements RepositoryFactory {

    @Override
    public AbstractRepository buildRepository(KRepository lRepository) {
        KettleDatabaseRepository repository = new KettleDatabaseRepository();
        // 配置资源库数据库连接信息
        DatabaseMeta databaseMeta = new DatabaseMeta(
                lRepository.getRepName(),
                lRepository.getDbType(),
                lRepository.getDbAccess(),
                lRepository.getDbHost(),
                lRepository.getDbName(),
                lRepository.getDbPort(),
                lRepository.getDbUsername(),
                lRepository.getDbPassword()
        );
        //启用连接池
        /*databaseMeta.setUsingConnectionPool(true);
        databaseMeta.setInitialPoolSize(20);
        databaseMeta.setMaximumPoolSize(100);*/
        //连接池其他属性
        databaseMeta.setAttributes(setProperties(databaseMeta.getAttributes(), databaseMeta.getPluginId()));
        // 配置资源库
        KettleDatabaseRepositoryMeta repositoryMeta = new KettleDatabaseRepositoryMeta();
        repositoryMeta.setConnection(databaseMeta);
        repositoryMeta.setName(lRepository.getRepName());
        repository.init(repositoryMeta);
        try {
            repository.connect(lRepository.getRepUsername(), lRepository.getRepPassword());
        } catch (KettleException e) {
            e.printStackTrace();
            log.warn("连接数据库资源库失败, 错误信息:{}, 连接信息为:{}", e.getMessage(), lRepository);
            return null;
        }
        return repository;
    }

    /**
     * 设置数据库属性，同客户端数据库配置界面
     *
     * @return
     */
    public Properties setProperties(Properties properties, String databaseType) {
        //properties.setProperty("PORT_NUMBER", "3306");
        //** 一般 **//*
        //使用结果流游标
        properties.setProperty("STREAM_RESULTS", "");
        //** -------------------------------------------------------------------- **//*

        /** 高级配置 **/
        //支持时间戳数据类型
        //properties.setProperty("SUPPORTS_TIMESTAMP_DATA_TYPE", "Y");
        //支持布尔数据类型
        //properties.setProperty("SUPPORTS_BOOLEAN_DATA_TYPE", "Y");
        //标识符使用引号括起来
        properties.setProperty("QUOTE_ALL_FIELDS", "N");
        //强制标识符为小写
        properties.setProperty("FORCE_IDENTIFIERS_TO_LOWERCASE", "N");
        //强制标识符大写
        properties.setProperty("FORCE_IDENTIFIERS_TO_UPPERCASE", "N");
        //properties.setProperty("PRESERVE_RESERVED_WORD_CASE", "");
        //默认模式名，在没有其他模式名时启用
        //properties.setProperty("PREFERRED_SCHEMA_NAME", "");
        //连接成功以后要执行的SQL，多条使用分号隔开
        //properties.setProperty("SQL_CONNECT", "");
        /** -------------------------------------------------------------------- **/

        /** 选项 命名参数**/
        if(databaseType.equals("MYSQL")){
            //最小交互数据量，如设置为10的话，查询100条数据，需要和数据库交互10次
            properties.setProperty("EXTRA_OPTION_MYSQL.defaultFetchSize", "500");
            properties.setProperty("EXTRA_OPTION_MYSQL.useCursorFetch", "true");
            //配置字符集
            properties.setProperty("EXTRA_OPTION_MYSQL.characterEncoding", "utf8");
            //不使用SSL连接MYSQL
            properties.setProperty("EXTRA_OPTION_MYSQL.useSSL", "false");
            // 配置断线自动重新链接
            //properties.setProperty("EXTRA_OPTION_MYSQL.autoReconnect", "true");
        }
        /** -------------------------------------------------------------------- **/

        /**连接池配置,布尔类型必须使用n/y，不区分大小写**/
        //是否使用连接池
        properties.setProperty("USE_POOLING", "Y");
        //初始化连接数目
        properties.setProperty("POOLING_INITIAL_POOL_SIZE", "10");
        //允许最大连接数
        properties.setProperty("POOLING_MAXIMUM_POOL_SIZE", "200");
        //控制 PoolGuard 是否允许访问底层连接
        //properties.setProperty("POOLING_accessToUnderlyingConnectionAllowed", "");
        //连接池创建的默认连接目录
        //properties.setProperty("POOLING_defaultCatalog", "");
        //是否只读
        properties.setProperty("POOLING_defaultReadOnly", "N");
        //此池创建的连接的默认 TransactionIsolation 状态。
        //properties.setProperty("POOLING_defaultTransactionIsolation", "");
        //启动池时创建的初始连接数。
        //properties.setProperty("POOLING_initialSize", "");
        //是否自动提交
        properties.setProperty("POOLING_defaultAutoCommit", "Y");
        //连接池支持的最大连接数
        properties.setProperty("POOLING_maxActive", "80");
        //连接池中最多可空闲maxIdle个连接
        properties.setProperty("POOLING_maxIdle", "10");
        //可以同时从语句池中分配的打开语句的最大数量，或者为零表示无限制。
        properties.setProperty("POOLING_maxOpenPreparedStatements", "0");
        //连接池中连接用完时,新的请求等待时间,毫秒，-1，表示无限等待，直到超时为止
        properties.setProperty("POOLING_maxWait", "-1");
        //连接池中最小空闲连接数，当连接数少于此值时，连接池会创建连接来补充到该值的数量
        properties.setProperty("POOLING_minIdle", "10");
        //为此池启用准备好的语句池
        //properties.setProperty("POOLING_poolPreparedStatements", "");
        //标记以在超过 removeAbandonedTimeout 时删除放弃的连接
        //properties.setProperty("POOLING_removeAbandoned", "");
        //可以删除放弃的连接之前的超时时间（以秒为单位）。
        //properties.setProperty("POOLING_removeAbandonedTimeout", "");
        //申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        properties.setProperty("POOLING_testOnBorrow", "Y");
        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        properties.setProperty("POOLING_testOnReturn", "N");
        //建议配置为Y，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        properties.setProperty("POOLING_testWhileIdle", "N");
        //空闲对象驱逐线程运行之间休眠的毫秒数。 当为非肯定时，不会运行空闲的对象驱逐线程
        //properties.setProperty("POOLING_timeBetweenEvictionRunsMillis", "");
        //验证数据库连接的查询语句
        properties.setProperty("POOLING_validationQuery", "select 2");
        /** -------------------------------------------------------------------- **/
        /** 集群配置 **/
        //是否为集群
        //properties.setProperty("IS_CLUSTERED", "N");
        return properties;
    }
}
