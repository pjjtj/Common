package com.core.common.jdbc.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import com.core.common.jdbc.bean.PageResult;
import com.core.common.jdbc.bean.SqlBean;
import com.core.common.jdbc.dao.EntityDao;
import com.core.common.util.string.EntityTools;
import com.core.common.util.string.SqlTools;

/**
 * 支持注解，若实体没有注解，实体类名需要按照驼峰命名，属性与数据库字段一致不区分大小写
 * @author 彭佳佳
 * @data 2018年3月7日
 * @param <T>
 */
public class EntityDaoImpl<T> extends JdbcDaoImpl implements EntityDao<T> {
	
	/**
	 * 设置一些操作的常量
	 */
	private static final String SQL_INSERT = "insert";
	private static final String SQL_UPDATE = "update";
	private static final String SQL_DELETE = "delete";
	private static final int BATCH_PAGE_SIZE=1000;
	
	/**
	 * 泛型
	 */
	private Class<T> entityClass;

	/**
	 * 表名
	 */
	private String simpleName;

	@SuppressWarnings("unchecked")
	public EntityDaoImpl() {
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		entityClass = (Class<T>) type.getActualTypeArguments()[0];
		simpleName = EntityTools.getTableName(entityClass);
	}

	@Override
	public void save(T entity) throws Exception  {
		String sql = this.makeSql(SQL_INSERT);
		Object[] args = this.setArgs(entity, SQL_INSERT);
		int[] argTypes = this.setArgTypes(entity, SQL_INSERT);
		jdbcTemplate.update(sql.toString(), args, argTypes);
	}
	
	@Override
	public void update(T entity) throws Exception {
		String sql = this.makeSql(SQL_UPDATE);
		Object[] args = this.setArgs(entity, SQL_UPDATE);
		int[] argTypes = this.setArgTypes(entity, SQL_UPDATE);
		jdbcTemplate.update(sql, args, argTypes);
	}
	
	@Override
	public void batchSave(List<T> objs) throws Exception  {
		if(objs==null||objs.size()==0) {
			return;
		}
		//分页操作
		String sql = this.makeSql(SQL_INSERT);
		int[] argTypes = this.setArgTypes(objs.get(0), SQL_INSERT);
		Integer j=0;
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (int i=0;i<objs.size();i++) {
			batchArgs.add(this.setArgs(objs.get(i), SQL_INSERT));
			j++;
			if(j.intValue()==BATCH_PAGE_SIZE) {
				jdbcTemplate.batchUpdate(sql, batchArgs,argTypes);
				batchArgs = new ArrayList<Object[]>();
				j=0;
			}
		}
		jdbcTemplate.batchUpdate(sql, batchArgs,argTypes);
	}
	
	@Override
	public void batchUpdate(List<T> objs) throws Exception {
		//分页操作
		String sql = this.makeSql(SQL_UPDATE);
		int[] argTypes = this.setArgTypes(objs.get(0), SQL_UPDATE);
		Integer j=0;
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (int i=0;i<objs.size();i++) {
			batchArgs.add(this.setArgs(objs.get(i), SQL_UPDATE));
			j++;
			if(j.intValue()==BATCH_PAGE_SIZE) {
				jdbcTemplate.batchUpdate(sql, batchArgs,argTypes);
				batchArgs = new ArrayList<Object[]>();
				j=0;
			}
		}
		jdbcTemplate.batchUpdate(sql, batchArgs,argTypes);
	}

	@Override
	public void delete(T entity) throws Exception  {
		String sql = this.makeSql(SQL_DELETE);
		Object[] args = this.setArgs(entity, SQL_DELETE);
		int[] argTypes = this.setArgTypes(entity, SQL_DELETE);
		jdbcTemplate.update(sql, args, argTypes);
	}
	
	@Override
	public void batchDelete(List<T> objs) throws Exception  {
		String sql = this.makeSql(SQL_DELETE);
		int[] argTypes = this.setArgTypes(objs.get(0), SQL_DELETE);
		Integer j=0;
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (int i=0;i<objs.size();i++) {
			batchArgs.add(this.setArgs(objs.get(i), SQL_DELETE));
			j++;
			if(j.intValue()==BATCH_PAGE_SIZE) {
				jdbcTemplate.batchUpdate(sql, batchArgs,argTypes);
				batchArgs = new ArrayList<Object[]>();
				j=0;
			}
		}
		jdbcTemplate.batchUpdate(sql, batchArgs,argTypes);
	}

	@Override
	public void deleteById(Serializable id) throws Exception {
		String primaryKey = "id";
		Field[] fields = entityClass.getDeclaredFields();
		primaryKey = EntityTools.getPrimaryKey(fields);
		String sql = " DELETE FROM " + simpleName + " WHERE "+primaryKey+" = ?";
		jdbcTemplate.update(sql, id);
	}
	
	@Override
	public void deleteByIds(List<Serializable> ids) throws Exception {
		if(ids!=null&&ids.size()>0) {
			String primaryKey = "id";
			Field[] fields = entityClass.getDeclaredFields();
			primaryKey = EntityTools.getPrimaryKey(fields);
			StringBuilder sql = new StringBuilder();
			sql.append(" DELETE FROM " + simpleName + " WHERE "+primaryKey+" in (");
			for(int i=0;i<ids.size();i++) {
				if(i==ids.size()-1) {
					sql.append("?)");
				}else {
					sql.append("?,");
				}
			}
			jdbcTemplate.update(sql.toString(), ids.toArray());
		}
	}

	@Override
	public void deleteBy(Map<String, Object> filterMap) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM " + simpleName + " WHERE 1=1");
		SqlBean sqlBean = SqlTools.SqlHelp(filterMap, null, entityClass);
		if(!StringUtils.isEmpty(sqlBean.getSqlString())) {
			sql.append(sqlBean.getSqlString());
		}
		Object[] args = sqlBean.getAgrs();
		int[] argTypes = sqlBean.getTypes();
		jdbcTemplate.update(sql.toString(), args, argTypes);
	}
	
	@Override
	public void deleteAll() throws Exception {
		String sql = " TRUNCATE TABLE " + simpleName;
		jdbcTemplate.execute(sql);
	}

	@Override
	public T findById(Serializable id) throws Exception {
		String primaryKey = "id";
		Field[] fields = entityClass.getDeclaredFields();
		primaryKey = EntityTools.getPrimaryKey(fields);
		String sql = "SELECT * FROM " + simpleName + " WHERE "+primaryKey+" = ?";
		RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
		return jdbcTemplate.query(sql, rowMapper, id).get(0);
	}

	@Override
	public List<T> findByIds(List<Serializable> ids) throws Exception {
		String primaryKey = "id";
		Field[] fields = entityClass.getDeclaredFields();
		primaryKey = EntityTools.getPrimaryKey(fields);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM " + simpleName + " WHERE "+primaryKey+" in (");
		for(int i=0;i<ids.size();i++) {
			if(i==ids.size()-1) {
				sql.append("?)");
			}else {
				sql.append("?,");
			}
		}
		RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
		return jdbcTemplate.query(sql.toString(), ids.toArray() , rowMapper);
	}
	
	@Override
	public List<T> findAll() throws Exception {
		String sql = "SELECT * FROM " + simpleName;
		RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
		return jdbcTemplate.query(sql, rowMapper);
	}

	@Override
	public List<T> findBy(Map<String, Object> filterMap) throws Exception {
		return findBy(filterMap,null);
	}

	@Override
	public List<T> findBy(Map<String, Object> filterMap, LinkedHashMap<String, String> sortMap) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM " + simpleName + " WHERE 1=1");
		SqlBean sqlBean = SqlTools.SqlHelp(filterMap, sortMap, entityClass);
		if(!StringUtils.isEmpty(sqlBean.getSqlString())) {
			sql.append(sqlBean.getSqlString());
		}
		Object[] args = sqlBean.getAgrs();
		int[] argTypes = sqlBean.getTypes();
		System.out.println(sql);
		return jdbcTemplate.query(sql.toString(), args, argTypes,BeanPropertyRowMapper.newInstance(entityClass));
	}

	@Override
	public PageResult<T> findByPageList(int pageNo, int pageSize) throws Exception {
		List<T> list = this.find(pageNo, pageSize, null, null);
		int totalRow = this.count(null);
		return new PageResult<T>(list, totalRow, pageNo, pageSize);
	}

	@Override
	public PageResult<T> findByPageList(int pageNo, int pageSize, Map<String, Object> where) throws Exception {
		List<T> list = this.find(pageNo, pageSize, where, null);
		int totalRow = this.count(where);
		return new PageResult<T>(list, totalRow, pageNo, pageSize);
	}

	@Override
	public PageResult<T> findByPageList(int pageNo, int pageSize, LinkedHashMap<String, String> orderby) throws Exception {
		List<T> list = this.find(pageNo, pageSize, null, orderby);
		int totalRow = this.count(null);
		return new PageResult<T>(list, totalRow, pageNo, pageSize);
	}

	@Override
	public PageResult<T> findByPageList(int pageNo, int pageSize, Map<String, Object> where,
			LinkedHashMap<String, String> orderby) throws Exception {
		List<T> list = this.find(pageNo, pageSize, where, orderby);
		int totalRow = this.count(where);
		return new PageResult<T>(list, totalRow, pageNo, pageSize);
	}

	/**
	 * 组装SQL
	 */
	private String makeSql(String sqlFlag) {
		StringBuffer sql = new StringBuffer();
		Field[] fields = entityClass.getDeclaredFields();
		if (sqlFlag.equals(SQL_INSERT)) {
			sql.append(" INSERT INTO " + simpleName);
			sql.append("(");
			for (int i = 0; fields != null && i < fields.length; i++) {
				fields[i].setAccessible(true); // 暴力反射
				String column = EntityTools.getColumnName(fields[i]);//获取属性对应字段名，没有注解默认按照属性名。有Column注解，获取Column的name作为字段名
				sql.append(column).append(",");
			}
			sql = sql.deleteCharAt(sql.length() - 1);
			sql.append(") VALUES (");
			for (int i = 0; fields != null && i < fields.length; i++) {
				sql.append("?,");
			}
			sql = sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		} else if (sqlFlag.equals(SQL_UPDATE)) {
			String primaryKey = "id";
			sql.append(" UPDATE " + simpleName + " SET ");
			for (int i = 0; fields != null && i < fields.length; i++) {
				fields[i].setAccessible(true); // 暴力反射
				String column = EntityTools.getColumnName(fields[i]);//获取属性对应字段名，没有注解默认按照属性名。有Column注解，获取Column的name作为字段名
				if (EntityTools.isPrimaryKey(fields[i])) { // id 代表主键
					primaryKey = column;
					continue;
				}
				sql.append(column).append("=").append("?,");
			}
			sql = sql.deleteCharAt(sql.length() - 1);
			sql.append(" WHERE "+primaryKey+" = ?");
		} else if (sqlFlag.equals(SQL_DELETE)) {
			String primaryKey = "id";
			for (int i = 0; fields != null && i < fields.length; i++) {
				fields[i].setAccessible(true); // 暴力反射
				String column = EntityTools.getColumnName(fields[i]);//获取属性对应字段名，没有注解默认按照属性名。有Column注解，获取Column的name作为字段名
				if (EntityTools.isPrimaryKey(fields[i])) { // id 代表主键
					primaryKey = column;
					break;
				}
			}
			sql.append(" DELETE FROM " + simpleName + " WHERE "+primaryKey+" = ?");
		}
		System.out.println("SQL=" + sql);
		return sql.toString();
	}

	/**
	 * 设置参数 
	 */
	private Object[] setArgs(T entity, String sqlFlag) {
		Field[] fields = entityClass.getDeclaredFields();
		if (sqlFlag.equals(SQL_INSERT)) {
			Object[] args = new Object[fields.length];
			for (int i = 0; args != null && i < args.length; i++) {
				try {
					fields[i].setAccessible(true); // 暴力反射
					args[i] = fields[i].get(entity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return args;
		} else if (sqlFlag.equals(SQL_UPDATE)) {
			Object[] args = new Object[fields.length];
			Object primaryValue = new Object();
			int j = 0;
			for (int i = 0; fields != null && i < fields.length; i++) {
				try {
					fields[i].setAccessible(true); // 暴力反射
					if (EntityTools.isPrimaryKey(fields[i])) { // id 代表主键
						primaryValue = fields[i].get(entity);
						continue;
					}
					args[j]=fields[i].get(entity);
					j++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			args[args.length - 1] = primaryValue;
			return args;
		} else if (sqlFlag.equals(SQL_DELETE)) {
			Object primaryValue = new Object();
			for (int i = 0; fields != null && i < fields.length; i++) {
				try {
					fields[i].setAccessible(true); // 暴力反射
					if (EntityTools.isPrimaryKey(fields[i])) { // id 代表主键
						primaryValue = fields[i].get(entity);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Object[] args = new Object[1]; // 长度是1
			try {
				args[0] = primaryValue;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return args;
		}
		return null;

	}

	/**
	 * 设置参数类型(缺少的用到了再添加)
	 */
	private int[] setArgTypes(T entity, String sqlFlag) {
		Field[] fields = entityClass.getDeclaredFields();
		if (sqlFlag.equals(SQL_INSERT)) {
			int[] argTypes = new int[fields.length];
			try {
				for (int i = 0; argTypes != null && i < argTypes.length; i++) {
					argTypes[i] = SqlTools.getTypes(fields[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return argTypes;
		} else if (sqlFlag.equals(SQL_UPDATE)) {
			int[] tempArgTypes = new int[fields.length];
			int[] argTypes = new int[fields.length];
			try {
				for (int i = 0; tempArgTypes != null && i < tempArgTypes.length; i++) {
					tempArgTypes[i] = SqlTools.getTypes(fields[i]);
				}
				System.arraycopy(tempArgTypes, 1, argTypes, 0, tempArgTypes.length - 1); // 数组拷贝
				argTypes[argTypes.length - 1] = tempArgTypes[0];

			} catch (Exception e) {
				e.printStackTrace();
			}
			return argTypes;

		} else if (sqlFlag.equals(SQL_DELETE)) {
			int[] argTypes = new int[1]; // 长度是1
			try {
				argTypes[0] = SqlTools.getTypes(fields[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return argTypes;
		}
		return null;
	}

	private List<T> find(int pageNo, int pageSize, Map<String, Object> filterMap, LinkedHashMap<String, String> sortMap) throws Exception {
		StringBuffer sql = new StringBuffer(" SELECT * from " + simpleName +" WHERE　1=1 ");
		SqlBean sqlBean = SqlTools.SqlHelp(filterMap, sortMap, entityClass);
		if(!StringUtils.isEmpty(sqlBean.getSqlString())) {
			sql.append(sqlBean.getSqlString());
		}
		sql.append(" LIMIT "+((pageNo-1)*pageSize)+","+pageSize);
		System.out.println("SQL=" + sql);
		Object[] args = sqlBean.getAgrs();
		int[] argTypes = sqlBean.getTypes();
		RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
		return jdbcTemplate.query(sql.toString(), args, argTypes, rowMapper);
	}

	private int count(Map<String, Object> filterMap) throws Exception {
		StringBuffer sql = new StringBuffer(" SELECT COUNT(*) FROM " + simpleName +" WHERE 1=1 ");
		SqlBean sqlBean = SqlTools.SqlHelp(filterMap, null, entityClass);
		if(!StringUtils.isEmpty(sqlBean.getSqlString())) {
			sql.append(sqlBean.getSqlString());
		}
		System.out.println("SQL=" + sql);
		Object[] args = sqlBean.getAgrs();
		int[] argTypes = sqlBean.getTypes();
		return jdbcTemplate.queryForObject(sql.toString(),args,argTypes, Integer.class);
	}
	
}
