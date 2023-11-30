package com.jason.datacollection.configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Value("${spring.datasource.driver-class-name}")
    String driverClassName;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(getDbType()));
        return interceptor;
    }

    public DbType getDbType() {
        DbType dbType = null;
        if (driverClassName.equals("oracle.jdbc.driver.OracleDriver") || driverClassName.equals("oracle.jdbc.OracleDriver")) {
            dbType = DbType.ORACLE;
        } else if (driverClassName.equals("mysql.jdbc.Driver") || driverClassName.equals("com.mysql.jdbc.Driver") || driverClassName.equals("com.mysql.cj.jdbc.Driver") || driverClassName.equals("org.gjt.mm.mysql.Driver")) {
            dbType = DbType.MYSQL;
        }
        return dbType;

    }
}
