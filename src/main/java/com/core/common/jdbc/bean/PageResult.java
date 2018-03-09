package com.core.common.jdbc.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 分页返回通用类
 * 
 * @author 彭佳佳
 * @data 2018年3月6日
 * @param <T>
 */
@SuppressWarnings("serial")
@Data
public class PageResult<T> implements Serializable {
	private Integer allCount;
	private Integer pageNo;
	private Integer pageSize;
	private Integer pageCount;
	private List<T> list;

	public PageResult() {
	};

	public PageResult(List<T> list, Integer allCount, Integer pageNo, Integer pageSize) {
		this.allCount = allCount;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.pageCount = (allCount % pageSize != 0)?(allCount/pageSize+1):0;
		this.list = list;

	}
}
