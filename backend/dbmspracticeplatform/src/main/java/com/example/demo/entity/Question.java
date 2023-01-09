package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain=true)
public class Question {
	@TableId(type=IdType.AUTO)
	/* 试题编号 */
	private int id;

	/*教师编号*/
	private String teacherId;

	/* 所在数据库名称 */
	private String dbName;
	
	/* 试题类型 
	 * 1 表示 select语句评判；
	 * 2 表示 insert/update/delete语句评判；
	 * 3 表示 create table语句评判；
	 * 4 表示 alter table语句评判；
	 * 5 表示 create view语句评判；
	 * 6 表示 create index语句评判；
	 * 7 表示 create user/role语句评判；
	 * 8 表示 grant user语句评判；
	 * 9 表示 revoke user语句评判；
	 * 10 表示 grant table语句评判；
	 * 11 表示 revoke table语句评判；
	 * 12 表示 create function/procedure语句评判；
	 */
	private short questionClass;
	
	/* 试题标题 */
	private String title;
	
	/* 试题内容 */
	private String content;
	
	/* 目标对象名称 */
	private String targetName;
	
	/* 试题答案 */
	private String answer;
	
	/* 试题解析 */
	private String analysis;
	
	/* 题目评判初始化SQL */
	private String initSQL;
	
	/* 是否逻辑删除 */
	private boolean deleteflag;

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	@TableField(exist = false)
	private Teacher teacher;

	@TableField(exist = false)
	private int orderId;
}
