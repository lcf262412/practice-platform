package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Accessors(chain=true)
public class ExerInClass1 {


	/* 总得分 */
	private int total_score;

	/* 班级编号 */
	private String stuClassId;
	
	/* 试卷编号 */
	private int exerciseId;
	
	/* 是否处于考试模式 */
	private boolean isTest;
	
	/* 作答开始时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	
	/* 作答结束时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	@TableField(exist = false)
	private Exercise exercise;
}
