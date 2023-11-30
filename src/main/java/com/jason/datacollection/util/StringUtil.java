package com.jason.datacollection.util;

import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 字符串工具
 * <pre>
 *     继承spring的StringUtils工具,并实现自己的一些逻辑处理
 * </pre>
 *
 * @author lyf
 */
public class StringUtil extends StringUtils {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
