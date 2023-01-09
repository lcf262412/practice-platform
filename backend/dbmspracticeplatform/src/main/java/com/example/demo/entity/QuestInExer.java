package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class QuestInExer {
	/* 试卷编号 */
	private int exerciseId;
	
	/* 试题编号 */
	private int questionId;
	
	/* 试题序号 */
	private int orderId;
	
	/* 试题分数 */
	private int score;

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	@TableField(exist = false)
	private Question question;

	@TableField(exist = false)
	private int countquestion;

}
