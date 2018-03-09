package com.core.common.jdbc.dao.impl;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
/**
 * 
 * @author 彭佳佳
 * @data 2018年3月9日
 */
public abstract class BaseDaoImpl {
	/**
	 * 简化数据操作
	 */
	@Resource(name = "jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
