package com.core.common.jdbc.test.pojo;

import java.io.Serializable;
import java.util.Date;

import com.core.common.annotation.Column;
import com.core.common.annotation.Id;
import com.core.common.annotation.Table;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Table(name="Test_User")
public class TestUser implements Serializable {
	@Id
	@Column(name="id")
	private String id;
	@Column(name="userName")
	private String userName;
	@Column(name="age")
	private Integer age;
	@Column(name="birthday")
	private Date birthday;
}
