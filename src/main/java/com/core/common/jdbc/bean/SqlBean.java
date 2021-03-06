package com.core.common.jdbc.bean;

import lombok.Data;

/**
 * SQL 拼接条件bean
 * @author 彭佳佳
 * @data 2018年3月9日
 */
@Data
public class SqlBean {
	private StringBuffer sqlString;
	private int[] types;
	private Object[] agrs;
}
