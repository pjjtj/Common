package com.core.common.jdbc.pojo;

import java.util.UUID;

/**
 * 主键id生成器
 * @author 彭佳佳
 * @data 2018年3月7日
 */
public class IdGenerator {
    public static String newId() {
        return UUID.randomUUID().toString();
    }

    public static String newShortId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
