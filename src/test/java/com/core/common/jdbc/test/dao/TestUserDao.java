package com.core.common.jdbc.test.dao;

import java.util.List;

import com.core.common.jdbc.dao.EntityDao;
import com.core.common.jdbc.test.pojo.TestUser;

public interface TestUserDao extends EntityDao<TestUser>{
	public List<TestUser> test();
}
