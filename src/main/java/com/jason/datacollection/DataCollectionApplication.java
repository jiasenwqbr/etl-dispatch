package com.jason.datacollection;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jason.datacollection.entity.KScript;
import com.jason.datacollection.service.KScriptService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@EnableOpenApi
@EnableScheduling
@SpringBootApplication
@MapperScan("com.jason.datacollection.mapper")
@Slf4j
@EnableConfigurationProperties
public class DataCollectionApplication {

    @Autowired
    KScriptService scriptService;

    @Value("${drives-dir}")
    String drivesDir;

    public static void main(String[] args) {
        SpringApplication.run(DataCollectionApplication.class, args);
    }

    /**
     * 系统重启后，检查正在运行中的任务，添加至Quoz
     */
    @Bean
    @Order(3)
    public void startCollectionTask() {
        log.info("系统重启，检查需要执行的任务，并启动。");
        QueryWrapper<KScript> qw = new QueryWrapper();
        qw.eq("SCRIPT_STATUS", "1");
        List<KScript> list = scriptService.list(qw);
        list.forEach(kScript -> {
            scriptService.startCollectionTask(kScript);
        });
    }

    @Bean
    @Order(1)
    public void loadJar() {
        File folder = new File(drivesDir);
        if (!folder.exists()) {
            log.warn("驱动文件夹不存在，正在创建");
            folder.mkdirs();
            return;
        }
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            String fileExt = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
            if ("jar".equalsIgnoreCase(fileExt) || "war".equalsIgnoreCase(fileExt)) {
                log.info("加载外部驱动类：" + file.getName());
                // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹
                Method method = null;
                try {
                    method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                } catch (NoSuchMethodException | SecurityException e1) {
                    e1.printStackTrace();
                }
                //获取方法的访问权限以便写回
                boolean accessible = method.isAccessible();
                try {
                    method.setAccessible(true);
                    // 获取系统类加载器
                    URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                    URL url = file.toURI().toURL();
                    method.invoke(classLoader, url);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    method.setAccessible(accessible);
                }
            }
        }

    }
}
