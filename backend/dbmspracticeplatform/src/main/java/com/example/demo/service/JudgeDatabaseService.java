package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.JudgeDatabase;

public interface JudgeDatabaseService extends IService<JudgeDatabase> {
	
	/* 插入答题数据库 */
	Map<String, Object> insertJudgeDatabase(String name, String describe);
	
	/* 修改指定答题数据库的元数据说明 */
	Map<String, Object> modifyJudgeDatabase(String name, String describe);
	
	/* 查询指定答题数据库的元数据说明 */
	Map<String, Object> getJudgeDatabaseByName(String name);
	
	/* 查询教师用户的全部答题数据库信息 */
	List<Map<String, Object>> getJudgeDatabasesByteacherId(String userId);
	
	/* 删除答题数据库信息 */
	Map<String, Object> deleteJudgeDatabaseByName(String name);

}
