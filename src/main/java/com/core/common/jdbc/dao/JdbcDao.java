package com.core.common.jdbc.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/**
 * JDBC封装类支持，原生sql操作。
 * @author 彭佳佳
 * @data 2018年3月9日
 */
public interface JdbcDao {
	
	/**
	 * sql添加并返回id
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public String insertForId( String sql) throws Exception;
	public String insertForId( String sql,  Object[] args) throws Exception;
	/**
	 * sql批量添加返回执行成功数量
	 * @param sql
	 * @param batchArgs
	 * @return
	 * @throws Exception
	 */
	public int batchInsert(String sql, List<Object[]> batchArgs) throws Exception;
	public int batchInsert(String sql, List<Object[]> batchArgs, int[] types) throws Exception;
	public int batchInsert(String sql, List<Object[]> batchArgs, int batchPageSize) throws Exception;
	public int batchInsert(String sql, List<Object[]> batchArgs, int[] types, int batchPageSize) throws Exception;
	
	/**
	 * sql批量更新操作
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public int update(String sql, Object[] args) throws Exception;
	
	/**
	 * sql查询
	 * @param sql
	 * @param args
	 * @param rse
	 * @return
	 * @throws Exception
	 */
	public <T> T query(String sql, ResultSetExtractor<T> rse) throws Exception;
	public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws Exception;
	
	public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws Exception;
	public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws Exception;
	
	public <T> List<T> query(String sql, Class<T> elementType) throws Exception;
	public <T> List<T> query(String sql, Object[] args, Class<T> elementType) throws Exception;
	
	/**
	 * sql查询 Java原生对象类型，不支持自定义对象类型
	 * @param sql
	 * @param requiredType
	 * @return
	 * @throws Exception
	 */
	public <T> T queryForObject(String sql, Class<T> requiredType) throws Exception;
	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws Exception;
	public <T> List<T> queryForList(String sql, Class<T> elementType) throws Exception;
	public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws Exception;
	/**
	 * 返回Map集合，可按照key自行循环组装对象
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryForList(String sql, Object[] args) throws Exception;
	
}
