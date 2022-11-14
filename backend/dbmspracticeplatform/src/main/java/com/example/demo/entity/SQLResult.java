package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain=true)
public class SQLResult {
	private SysUser sysUser;
	
	private Teacher teacher;
	
	private StuClass stuClass;
	
	private Student student;
	
	private Database database;
	
	private JudgeDatabase judgeDatabase;
	
	private Exercise exercise;
	
	private Question question;
	
	private QuestInExer questInExer;
	
	private ExerInClass exerInClass;
	
	private StuAnswer stuAnswer;
	
	//学生是否将某套试卷上的题目做完
	private boolean finish;
	
	//学生是否在某套试卷上满分
	private boolean full;
	
	//学生在某套试卷上的分数
	private int scoreSum;

}
