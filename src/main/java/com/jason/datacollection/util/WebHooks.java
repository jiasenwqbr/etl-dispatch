package com.jason.datacollection.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "webhooks")
@Data
public class WebHooks {

    private boolean start;
    private String dingtalk;
    private String wechat;

}
