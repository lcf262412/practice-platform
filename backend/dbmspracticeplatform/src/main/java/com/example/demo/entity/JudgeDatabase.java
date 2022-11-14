package com.example.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class JudgeDatabase {
	/* 答题数据库名称,同数据库名称 */
	private String name;
	
	/* 答题数据库元数据描述 */
	private String describe;

}
