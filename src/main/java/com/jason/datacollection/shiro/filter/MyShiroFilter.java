package com.jason.datacollection.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * url过滤配置
 *
 * @author admin
 */
@Configuration
@Slf4j
public class MyShiroFilter {

    @Value("${shouldLogin}")
    boolean shouldLogin;

    /**
     * 对shiro的过滤进行设置
     *
     * @return {@link ShiroFilterChainDefinition}
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        // 可以匿名访问静态资源
        chainDefinition.addPathDefinition("favicon.ico", "anon");
        chainDefinition.addPathDefinition("/static/css/**", "anon");
        chainDefinition.addPathDefinition("/static/fonts/**", "anon");
        chainDefinition.addPathDefinition("/static/img/**", "anon");
        chainDefinition.addPathDefinition("/img/**", "anon");
        chainDefinition.addPathDefinition("/static/js/**", "anon");
        chainDefinition.addPathDefinition("/static/lib/**", "anon");
        // 设置登录功能可以访问
        chainDefinition.addPathDefinition("/dataCollection/login/in", "anon");
        // 设置swagger可以访问
        chainDefinition.addPathDefinition("/swagger-ui/index.html", "anon");
        chainDefinition.addPathDefinition("/webjars/**", "anon");
        chainDefinition.addPathDefinition("/v2/**", "anon");
        chainDefinition.addPathDefinition("/swagger-resources/**", "anon");
        // 设置需要管理员才能访问
        chainDefinition.addPathDefinition("/sys/user/getUserByUsername.do", "authc");
        chainDefinition.addPathDefinition("/sys/user/**", "authc,roles[admin]");

        //去除登录
        //chainDefinition.addPathDefinition("/**","anon");
        /* roles[python]中定义需要当前角色才能具有访问权限, 自定义Filter中的Object o就是roles["角色1","角色2"]中的数据
        chainDefinition.addPathDefinition("/my/python", "authc,roles[python]");*/

        // 需要登录才能访问
        if (shouldLogin) {
            chainDefinition.addPathDefinition("/**", "authc");
            log.info("开启登录配置");
        } else {
            log.info("无需登录");
        }
        return chainDefinition;
    }
}
