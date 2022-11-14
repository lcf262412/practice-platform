package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class Exercise {
	@TableId(type = IdType.AUTO)
	/* 试卷编号 */
	private int id;
	
	/* 创建教师编号 */
	private String teacherId;
	
	/* 试卷名称 */
	private String name;
	
	/* 试卷描述 */
	private String describe;
	
	/* 是否对其他教师开放 */
	private boolean isPublic;
	
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

}
