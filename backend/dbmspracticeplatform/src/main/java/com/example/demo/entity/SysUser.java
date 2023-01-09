package com.example.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class SysUser {
	
	/* 用户编号 */
	private String id;
	
	/* 用户密码 */
	private String pwd;
	
	/* 用户类别 1:管理员 2:教师 3:学生 4:访客*/
	private short userClass;
}
