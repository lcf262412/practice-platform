package com.example.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class StuClass {
	
	/* 班级编号 */
	private String id;
	
	/* 教师编号 */
	private String teacherId;
	
	/* 上课学期 */
	private String semester;

	
}
