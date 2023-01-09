package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class StuAnswer {
	/* 试卷编号 */
	private int exerciseId;
	
	/* 题目编号 */
	private int questionId;
	
	/* 学生编号 */
	private String studentId;
	
	/* 学生答案 */
	private String answer;
	
	/* 是否正确 */
	private boolean isRight;
	
	/* 作答思路 */
	private String idea;
	
	/* 最终得分 */
	private int score;

	@TableField(exist = false)
	private  int sum;

	@TableField(exist = false)
	private  String name;

	@TableField(exist = false)
	private  int countanswer;

	@TableField(exist = false)
	private  int avgscore;
}
