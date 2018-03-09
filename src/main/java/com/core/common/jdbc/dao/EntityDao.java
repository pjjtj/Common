package com.core.common.jdbc.dao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.core.common.jdbc.bean.PageResult;

public interface EntityDao<T> extends JdbcDao {
	/**
	 * 插入指定的持久化对象
	 * 
	 * @param obj
	 * @return
	 */
	void save(T obj) throws Exception ;

	/**
	 * 修改指定的持久化对象 
	 * 
	 * @param obj
	 */
	void update(T obj) throws Exception ;

	/**
	 * 批量保存指定的持久化对象
	 * 
	 * @param objs
	 */
	void batchSave(List<T> objs) throws Exception ;

	/**
	 * 批量更新指定的持久化对象
	 * 
	 * @param objs
	 */
	void batchUpdate(List<T> objs) throws Exception ;

	/**
	 * 删除指定的持久化对象
	 * 
	 * @param id
	 */
	void delete(T obj) throws Exception ;

	/**
	 * 批量删除指定的持久化对象
	 * 
	 * @param objs
	 */
	void batchDelete(List<T> objs) throws Exception ;

	/**
	 * 删除指定id的持久化对象
	 * 
	 * @param id
	 */
	void deleteById(Serializable id) throws Exception ;

	/**
	 * 批量删除指定ids的持久化对象
	 * 
	 * @param ids
	 */
	void deleteByIds(List<Serializable> ids) throws Exception ;
	
	/**
	 * 根据条件删除实体对象
	 * @param filterMap
	 * @throws Exception
	 */
	void deleteBy(Map<String,Object> filterMap) throws Exception;

	/**
	 * 全部删除持久化对象
	 */
	void deleteAll() throws Exception ;

	/**
	 * 根据ID检索持久化对象
	 */
	T findById(Serializable id) throws Exception ;
	
	/**
	 * 根据主键查询批量查询持久化对象
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<T> findByIds(List<Serializable> ids) throws Exception;

	/**
	 * 根据条件查询持久化对象
	 * @param filterMap
	 * @return
	 * @throws Exception
	 */
	List<T> findBy(Map<String,Object> filterMap) throws Exception;
	
	/**
	 * 根据条件排序查询持久化对象
	 * @param filterMap
	 * @param sortMap
	 * @return
	 * @throws Exception
	 */
	List<T> findBy(Map<String,Object> filterMap,LinkedHashMap<String,String> sortMap) throws Exception;

	/**
	 * 检索所有持久化对象
	 */
	List<T> findAll() throws Exception ;

	/**
	 * 检索指定页和指定条数的持久化对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 */
	PageResult<T> findByPageList(int pageNo, int pageSize) throws Exception ;

	/**
	 * 根据条件检索指定页和指定条数的持久化对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 */
	PageResult<T> findByPageList(int pageNo, int pageSize, Map<String, Object> filterMap) throws Exception ;

	/**
	 * 根据排序检索指定页和指定条数的持久化对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 */
	PageResult<T> findByPageList(int pageNo, int pageSize, LinkedHashMap<String, String> sortMap) throws Exception ;

	/**
	 * 根据条件和排序检索指定页和指定条数的持久化对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 */
	PageResult<T> findByPageList(int pageNo, int pageSize, Map<String, Object> filterMap,LinkedHashMap<String, String> sortMap) throws Exception ;
}
