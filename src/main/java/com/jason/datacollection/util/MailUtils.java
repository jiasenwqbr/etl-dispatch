package com.jason.datacollection.util;

import cn.hutool.extra.mail.MailAccount;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mail")
@Data
public class MailUtils {

    private boolean start;
    //邮件服务器的SMTP地址
    private String host;
    // 邮件服务器的SMTP端口
    private int port;
    // 发件人（必须正确，否则发送失败）
    private String from;
    // 收件人
    private String recipient;
    // 用户名（注意：如果使用foxmail邮箱，此处user为qq号）
    private String user;
    // 密码（注意，某些邮箱需要为SMTP服务单独设置密码，详情查看相关帮助）
    private String pass;
    //使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。
    private boolean starttlsEnable;
    // 使用SSL安全连接
    private boolean sslEnable;//true
    // 指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
    private String socketFactoryClass;
    // 如果设置为true,未能创建一个套接字使用指定的套接字工厂类将导致使用java.net.Socket创建的套接字类, 默认值为true
    private String socketFactoryFallback;
    // 指定的端口连接到在使用指定的套接字工厂。如果没有设置,将使用默认端口456
    private int socketFactoryPort;
    // SMTP超时时长，单位毫秒，缺省值不超时
    private long timeout;
    // Socket连接超时值，单位毫秒，缺省值不超时
    private long connectionTimeout;

    public MailAccount createSendConfig() {
        MailAccount account = new MailAccount();
        account.setHost(host);
        account.setPort(port);
        account.setAuth(true);
        account.setFrom(from);
        account.setUser(user);
        account.setPass(pass);
        account.setSslEnable(sslEnable);
        return account;
    }

    public static void main(String[] args) {
        //MailAccount account = MailUtils.createSendConfig();
        //String send = MailUtil.send(account, CollUtil.newArrayList("chen.1076@qq.com"), "测试", "邮件来自Hutool测试", false);
        //System.out.println(send);
    }
}
