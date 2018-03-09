package com.core.common.jdbc.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.core.common.jdbc.dao.JdbcDao;

public abstract class JdbcDaoImpl extends BaseDaoImpl implements JdbcDao {

	/**
	 * 分页执行页大小
	 */
	private static final int BATCH_PAGE_SIZE = 1000;

	private static final Map<Integer, String> paramSymbol = new HashMap<Integer, String>();

	@Override
	public String insertForId(String sql) throws Exception {
		return this.insertForId(sql, null);
	}

	@Override
	public String insertForId(final String sql, final Object[] args) throws Exception {
		String id = jdbcTemplate.execute(new ConnectionCallback<String>() {
			@Override
			public String doInConnection(Connection con) throws SQLException, DataAccessException {
				PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);// 传入参数：Statement.RETURN_GENERATED_KEYS
				if (args != null) {
					for (int i = 1; i <= args.length; i++) {
						Object arg = args[i - 1];
						setParamater(ps, i, arg);
					}
				}
				ps.executeUpdate();// 执行sql
				ResultSet rs = ps.getGeneratedKeys();// 获取结果
				if (rs.next()) {
					return rs.getString(1);// 取得ID
				} else {
					return null;
				}
			}
		});
		return id;
	}

	@Override
	public int batchInsert(String sql, List<Object[]> batchArgs) throws Exception {
		return this.batchInsert(sql, batchArgs, null, 0);
	}

	@Override
	public int batchInsert(String sql, List<Object[]> batchArgs, int batchPageSize) throws Exception {
		return this.batchInsert(sql, batchArgs, null, batchPageSize);
	}

	@Override
	public int batchInsert(String sql, List<Object[]> batchArgs, int[] types) throws Exception {
		return this.batchInsert(sql, batchArgs, types, 0);
	}

	@Override
	public int batchInsert(String sql, List<Object[]> batchArgs, int[] types, int batchPageSize) throws Exception {
		if (batchPageSize <= 0) {
			batchPageSize = BATCH_PAGE_SIZE;
		}
		sql = sql.trim();
		if (sql.endsWith(";")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		int index = sql.toLowerCase().indexOf("values");
		if (index == -1) {
			throw new RuntimeException("此sql语句不能执行批量插入操作");
		}
		index = sql.indexOf("(", index);// 查找values后第一个左括号位置
		String sqlLeft = sql.substring(0, index);
		String sqlRight = sql.substring(index);
		int batchSize = batchArgs.size();
		int resultSize = 0;
		// 插入完整页的数据
		int wholeLength = batchSize / batchPageSize;
		StringBuffer sqlJoin = new StringBuffer();
		if (wholeLength > 0) {
			sqlJoin.append(sqlLeft);
			sqlJoin.append(sqlRight);
			for (int i = 0; i < batchPageSize - 1; i++) {
				sqlJoin.append(",").append(sqlRight);
			}
			List<Object> params = new ArrayList<Object>(batchPageSize * batchArgs.get(0).length);
			int[] argTypes = null;
			if (types != null) {
				argTypes = new int[batchPageSize * types.length];
				int length = types.length;
				for (int i = 0; i < batchPageSize; i++) {
					for (int j = 0; j < types.length; j++) {
						argTypes[i * length + j] = types[j];
					}
				}
			}
			for (int i = 0; i < wholeLength; i++) {
				params.clear();
				for (int j = 0; j < batchPageSize; j++) {
					Object[] args = batchArgs.get(i * batchPageSize + j);
					for (Object arg : args) {
						params.add(arg);
					}
				}
				if (argTypes == null) {
					resultSize += jdbcTemplate.update(sqlJoin.toString(), params.toArray());
				} else {
					resultSize += jdbcTemplate.update(sqlJoin.toString(), params.toArray(), argTypes);
				}
			}
		}
		// 插入剩余的数据
		int surplusSize = batchSize % batchPageSize;
		if (surplusSize > 0) {
			sqlJoin.setLength(0);
			sqlJoin.append(sqlLeft);
			sqlJoin.append(sqlRight);
			for (int i = 0; i < surplusSize - 1; i++) {
				sqlJoin.append(",").append(sqlRight);
			}
			List<Object> params = new ArrayList<Object>(surplusSize * batchArgs.get(0).length);
			int[] argTypes = null;
			if (types != null) {
				argTypes = new int[surplusSize * types.length];
				int length = types.length;
				for (int i = 0; i < surplusSize; i++) {
					for (int j = 0; j < types.length; j++) {
						argTypes[i * length + j] = types[j];
					}
				}
			}
			for (int j = 0; j < surplusSize; j++) {
				Object[] args = batchArgs.get(wholeLength * batchPageSize + j);
				for (Object arg : args) {
					params.add(arg);
				}
			}
			if (argTypes == null) {
				resultSize += jdbcTemplate.update(sqlJoin.toString(), params.toArray());
			} else {
				resultSize += jdbcTemplate.update(sqlJoin.toString(), params.toArray(), argTypes);
			}
		}
		return resultSize;
	}

	@Override
	public int update(String sql, Object[] args) throws Exception {
		Object[] obj = this.changeMessage(sql, args);
		return jdbcTemplate.update((String) obj[0], (Object[]) obj[1], (int[]) obj[2]);
	}

	@Override
	public <T> T query(String sql, ResultSetExtractor<T> rse) throws Exception {
		return jdbcTemplate.query(sql, rse);
	}

	@Override
	public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws Exception {
		Object[] obj = this.changeMessage(sql, args);
		return jdbcTemplate.query((String) obj[0], (Object[]) obj[1], (int[]) obj[2], rse);
	}

	@Override
	public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws Exception {
		return jdbcTemplate.query(sql, rowMapper);
	}

	@Override
	public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws Exception {
		Object[] obj = this.changeMessage(sql, args);
		return jdbcTemplate.query((String) obj[0], (Object[]) obj[1], (int[]) obj[2], rowMapper);
	}

	@Override
	public <T> List<T> query(String sql, Class<T> elementType) throws Exception {
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(elementType));
	}

	@Override
	public <T> List<T> query(String sql, Object[] args, Class<T> elementType) throws Exception {
		Object[] obj = this.changeMessage(sql, args);
		return jdbcTemplate.query((String) obj[0], (Object[]) obj[1], (int[]) obj[2],
				BeanPropertyRowMapper.newInstance(elementType));
	}

	@Override
	public <T> T queryForObject(String sql, Class<T> requiredType) throws Exception {
		return this.queryForObject(sql, null, requiredType);
	}

	@Override
	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws Exception {
		List<T> results = null;
		RowMapper<T> rowMapper = new SingleColumnRowMapper<T>(requiredType);
		if (args != null && args.length > 0) {
			Object[] obj = this.changeMessage(sql, args);
			sql = (String) obj[0];
			args = (Object[]) obj[1];
			int[] argTypes = (int[]) obj[2];
			results = jdbcTemplate.query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper, 1));
		} else {
			results = jdbcTemplate.query(sql, new RowMapperResultSetExtractor<T>(rowMapper, 1));
		}
		return DataAccessUtils.singleResult(results);
	}

	@Override
	public <T> List<T> queryForList(String sql, Class<T> elementType) throws Exception {
		return jdbcTemplate.queryForList(sql, elementType);
	}

	@Override
	public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws Exception {
		Object[] obj = this.changeMessage(sql, args);
		return jdbcTemplate.queryForList((String) obj[0], (Object[]) obj[1], (int[]) obj[2], elementType);
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object[] args) throws Exception {
		Object[] obj = this.changeMessage(sql, args);
		return jdbcTemplate.queryForList((String) obj[0], (Object[]) obj[1]);
	}

	private Object[] changeMessage(String sql, Object[] args) throws Exception {
		List<Object> params = new ArrayList<Object>();
		List<Integer> types = new ArrayList<Integer>();
		List<Object[]> paramList = new ArrayList<Object[]>();
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg.getClass().isArray()) {
				Object[] array = (Object[]) arg;
				if (array.length == 0) {
					throw new RuntimeException("Array param can not be empty!");
				}
				Integer type = null;
				for (Object obj : array) {
					if (type == null) {
						type = this.getTypes(obj);
					}
					params.add(obj);
					types.add(type);
				}
				if (array.length > 1) {
					paramList.add(new Object[] { i, getParamSymbol(array.length) });
				}
			} else if (Collection.class.isAssignableFrom(arg.getClass())) {
				Collection<?> list = (Collection<?>) arg;
				if (list.size() == 0) {
					throw new RuntimeException("Collection param can not be empty!");
				}
				Integer type = null;
				for (Object obj : list) {
					if (type == null) {
						type = this.getTypes(obj);
					}
					params.add(obj);
					types.add(type);
				}
				if (list.size() > 1) {
					paramList.add(new Object[] { i, getParamSymbol(list.size()) });
				}
			} else {
				params.add(arg);
				types.add(this.getTypes(arg));
			}
		}
		// 根据序号替换单个?为多个数组?
		if (!paramList.isEmpty()) {
			StringBuffer sqlb = new StringBuffer();
			String sqls = sql.toString();
			int count = -1;
			int index = -1;
			for (Object[] obj : paramList) {
				int paramIndex = (Integer) obj[0];
				while (true) {
					count++;
					index = sqls.indexOf("?", index + 1);
					if (index == -1) {
						throw new RuntimeException("Paramater size > '?' size!");
					}
					if (paramIndex == count) {
						sqlb.append(sqls.substring(0, index)).append((String) obj[1]);
						sqls = sqls.substring(index + 1);
						index = -1;
						break;
					}
				}
			}
			sqlb.append(sqls);
			sql = sqlb.toString();
		}

		int[] tys = new int[types.size()];
		for (int i = 0; i < types.size(); i++) {
			tys[i] = types.get(i);
		}
		return new Object[] { sql, params.toArray(), tys };
	}

	private int getTypes(Object arg) {
		if (String.class.equals(arg.getClass())) {
			return Types.VARCHAR;
		} else if (int.class.equals(arg.getClass()) || Integer.class.equals(arg.getClass())) {
			return Types.INTEGER;
		} else if (double.class.equals(arg.getClass()) || Double.class.equals(arg.getClass())) {
			return Types.DOUBLE;
		} else if (java.util.Date.class.isAssignableFrom(arg.getClass())) {
			return Types.TIMESTAMP;
		} else if (long.class.equals(arg.getClass()) || Long.class.equals(arg.getClass())) {
			return Types.BIGINT;
		} else if (float.class.equals(arg.getClass()) || Float.class.equals(arg.getClass())) {
			return Types.FLOAT;
		} else if (boolean.class.equals(arg.getClass()) || Boolean.class.equals(arg.getClass())) {
			return Types.BOOLEAN;
		} else if (short.class.equals(arg.getClass()) || Short.class.equals(arg.getClass())) {
			return Types.INTEGER;
		} else if (byte.class.equals(arg.getClass()) || Byte.class.equals(arg.getClass())) {
			return Types.INTEGER;
		} else if (BigDecimal.class.equals(arg.getClass())) {
			return Types.DECIMAL;
		} else {
			return Types.OTHER;
		}
	}

	private void setParamater(PreparedStatement ps, int i, Object arg) throws SQLException {
		if (String.class.equals(arg.getClass())) {
			ps.setString(i, (String) arg);
		} else if (int.class.equals(arg.getClass()) || Integer.class.equals(arg.getClass())) {
			ps.setInt(i, (Integer) arg);
		} else if (double.class.equals(arg.getClass()) || Double.class.equals(arg.getClass())) {
			ps.setDouble(i, (Double) arg);
		} else if (java.util.Date.class.isAssignableFrom(arg.getClass())) {
			ps.setTimestamp(i, new Timestamp(((Date) arg).getTime()));
		} else if (long.class.equals(arg.getClass()) || Long.class.equals(arg.getClass())) {
			ps.setLong(i, (Long) arg);
		} else if (float.class.equals(arg.getClass()) || Float.class.equals(arg.getClass())) {
			ps.setFloat(i, (Float) arg);
		} else if (boolean.class.equals(arg.getClass()) || Boolean.class.equals(arg.getClass())) {
			ps.setBoolean(i, (Boolean) arg);
		} else if (short.class.equals(arg.getClass()) || Short.class.equals(arg.getClass())) {
			ps.setShort(i, (Short) arg);
		} else if (byte.class.equals(arg.getClass()) || Byte.class.equals(arg.getClass())) {
			ps.setByte(i, (Byte) arg);
		} else if (BigDecimal.class.equals(arg.getClass())) {
			ps.setBigDecimal(i, (BigDecimal) arg);
		} else {
			throw new SQLException("参数" + i + "属于未知的参数类型！");
		}
	}

	private String getParamSymbol(int size) {
		String result = paramSymbol.get(size);
		if (result == null) {
			synchronized (paramSymbol) {
				StringBuffer sb = new StringBuffer("?");
				for (int i = 1; i < size; i++) {
					sb.append(",?");
				}
				result = sb.toString();
				paramSymbol.put(size, result);
			}
		}
		return result;
	}
}
