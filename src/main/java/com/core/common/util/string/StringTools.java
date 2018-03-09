package com.core.common.util.string;

/**
 * String工具类
 * @author 彭佳佳
 * @data 2018年3月7日
 */
public class StringTools {
	/**
	 * 字符串为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return ((str == null) || (str.trim().length() == 0));
	}

	/**
	 * 字符串非空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return (!(isEmpty(str)));
	}
}
