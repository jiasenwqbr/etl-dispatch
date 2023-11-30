package com.jason.datacollection.core.enums;

import com.jason.datacollection.core.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 运行结果枚举类
 *
 * @author lyf
 */
@Getter
@AllArgsConstructor
public enum RunResultEnum implements BaseEnum<String> {
    /**
     * 资源库方式运行
     */
    SUCCESS("1", "运行成功"),
    /**
     * 文件方式运行
     */
    FAIL("0", "运行失败"),
    RUN("2", "运行中");

    private String code;
    private String desc;

    public static RunResultEnum getEnum(String code) {
        return Arrays.stream(values()).filter(b -> Objects.equals(b.code, code)).findFirst().orElse(null);
    }

    public static String getEnumDesc(String code) {
        RunResultEnum e = getEnum(code);
        return e != null ? e.desc : null;
    }
}
