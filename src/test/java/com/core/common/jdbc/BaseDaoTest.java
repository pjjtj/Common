package com.core.common.jdbc;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.core.common.jdbc.pojo.IdGenerator;
import com.core.common.jdbc.test.dao.TestUserDao;
import com.core.common.jdbc.test.pojo.TestUser;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class BaseDaoTest {

	@Autowired
	private TestUserDao testUserDao ;
	
	// 测试是否取得数据库连接
//	@Test
//	public void testDataSource() throws SQLException {
//		@SuppressWarnings("resource")
//		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/applicationContext.xml");
//		DataSource dataSource = ctx.getBean(DataSource.class);
//		testUserDao = ctx.getBean(TestUserDao.class);
//		System.out.println(dataSource.getConnection());
//	}

	@Test
	public void testSave() throws Exception {
		TestUser test = new TestUser();
		test.setId(IdGenerator.newShortId());
		test.setAge(26);
		test.setBirthday(new Date());
		test.setUserName("JavaPjj2");
		testUserDao.save(test);
	}
	
	@Test
	public void testUpdate() throws Exception {
		TestUser test = new TestUser();
		test.setId("ed64ca0c5e654375bb4eaeb119007374");
		test.setAge(28);
		test.setBirthday(new Date("1993/01/07"));
		test.setUserName("JavaAA");
		testUserDao.update(test);
	}
	
	@Test
	public void testDelete() throws Exception {
		TestUser test = new TestUser();
		test.setId("7a4a8068ac3b4d969d42694333fb441b");
		test.setAge(28);
		test.setBirthday(new Date("1993/01/07"));
		test.setUserName("JavaAA");
		testUserDao.delete(test);
	}
	
	
	@Test
	public void testBatchSave() throws Exception {
		List<TestUser> testUsers = new ArrayList<TestUser>();
		for(int i=0;i<3500;i++) {
			TestUser test = new TestUser();
			test.setId(new DecimalFormat("0000").format(i));
			test.setAge(26);
			test.setBirthday(new Date());
			test.setUserName("JavaPjj"+i);
			testUsers.add(test);
		}
		testUserDao.batchSave(testUsers);
	}
	
	
	@Test
	public void testBatchUpdate() throws Exception {
		List<TestUser> testUsers = new ArrayList<TestUser>();
		for(int i=0;i<3500;i++) {
			TestUser test = new TestUser();
			test.setId(new DecimalFormat("0000").format(i));
			test.setAge(26);
			test.setBirthday(new Date());
			test.setUserName("JavaZN"+i);
			testUsers.add(test);
		}
		testUserDao.batchUpdate(testUsers);
	}
	
	@Test
	public void testBatchDelete() throws Exception {
		List<TestUser> testUsers = new ArrayList<TestUser>();
		for(int i=0;i<1000;i++) {
			TestUser test = new TestUser();
			test.setId(new DecimalFormat("0000").format(i));
			test.setAge(26);
			test.setBirthday(new Date());
			test.setUserName("JavaZN"+i);
			testUsers.add(test);
		}
		testUserDao.batchDelete(testUsers);
	}
	
	@Test
	public void testDeleteById() throws Exception {
		List<Serializable> ids = new ArrayList<Serializable>();
		for(int i=1000;i<2000;i++) {
			ids.add(new DecimalFormat("0000").format(i));
		}
		testUserDao.deleteByIds(ids);
	}
	
	@Test
	public void clearTable() throws Exception{
		testUserDao.deleteAll();
	}
	
	@Test
	public void findById() throws Exception{
		TestUser testUser = testUserDao.findById("2000");
		System.out.println(testUser.toString());
	}
	
	@Test
	public void findAll() throws Exception{
		List<TestUser> testUsers = testUserDao.findAll();
		for (TestUser testUser : testUsers) {
			System.out.println(testUser.toString());
		}
	}
	
	@Test
	public void testDeleteBy() throws Exception {
		Map<String,Object> filterMap = new HashMap<String,Object>();
		String[] ids = new String[]{"2000","2001","2002"};
		Map<String,Object> idsMap = new HashMap<String,Object>();
		idsMap.put("in", ids);
		filterMap.put("id", idsMap);
		Integer[] age = new Integer[]{26,29};
		Map<String,Object> ageMap = new HashMap<String,Object>();
		ageMap.put("between", age);
		filterMap.put("age", ageMap);
		List<TestUser> testUsers = testUserDao.findBy(filterMap);
		
		for (TestUser testUser : testUsers) {
			System.out.println(testUser.toString());
		}
	}
	
	
	@Test
	public void queryForObject() throws Exception{
		String sql = "select birthday from Test_User where id in ('2000','2500','3000')";
		Date testUser = testUserDao.queryForObject(sql, Date.class);
		System.out.println(testUser);
	}
	
	
	
	
}
