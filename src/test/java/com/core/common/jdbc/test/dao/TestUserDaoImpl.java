package com.core.common.jdbc.test.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.core.common.jdbc.dao.impl.EntityDaoImpl;
import com.core.common.jdbc.test.pojo.TestUser;

@SuppressWarnings("serial")
@Repository
public class TestUserDaoImpl extends EntityDaoImpl<TestUser> implements TestUserDao {
	
	
	
	public List<TestUser> test() {
		String sql = "select * from Test_User where id = '2000'";
		return this.getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(TestUser.class));
	}
}
