package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.JudgeDatabase;
import com.example.demo.entity.Question;
import com.example.demo.mapper.JudgeDatabaseMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.service.JudgeDatabaseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JudgeDatabaseServiceImpl extends ServiceImpl<JudgeDatabaseMapper, JudgeDatabase> implements JudgeDatabaseService {
	@Autowired
	private JudgeDatabaseMapper judgeDatabaseMapper;
	@Autowired
	private QuestionMapper questionMapper;
	
	/* 插入答题数据库 */
	@Override
	public Map<String, Object> insertJudgeDatabase(String name, String describe) {
		JudgeDatabase judgeDatabase = new JudgeDatabase();
		judgeDatabase.setName(name);
		judgeDatabase.setDescribe(describe);
		
		int mes=judgeDatabaseMapper.insert(judgeDatabase);
        Map<String,Object> modelMap = new HashMap<String, Object>();
        if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;
	}
	
	/* 修改指定答题数据库的元数据说明 */
	@Override
	public Map<String, Object> modifyJudgeDatabase(String name, String describe) {
		QueryWrapper<JudgeDatabase> judgeDatabaseWrapper = new QueryWrapper<>();
		judgeDatabaseWrapper.eq("name", name);
		JudgeDatabase judgeDatabase = judgeDatabaseMapper.selectOne(judgeDatabaseWrapper);
		
		judgeDatabase.setDescribe(describe);
		int mes = judgeDatabaseMapper.update(judgeDatabase, judgeDatabaseWrapper);
		Map<String,Object> modelMap = new HashMap<String, Object>();
		if(mes==1){
            modelMap.put("state","success");
        }
        else {
            modelMap.put("state","error");
        }

        return modelMap;
	}
	
	/* 查询指定答题数据库的元数据说明 */
	@Override
	public Map<String, Object> getJudgeDatabaseByName(String name) {
		Map<String,Object> modelMap = new HashMap<String, Object>();
		QueryWrapper<JudgeDatabase> judgeDatabaseWrapper = new QueryWrapper<>();
		judgeDatabaseWrapper.eq("name", name);
		JudgeDatabase judgeDatabase = judgeDatabaseMapper.selectOne(judgeDatabaseWrapper);
		
		if (judgeDatabase == null) {
			return modelMap;
		}
		
		modelMap.put("name", judgeDatabase.getName());
		modelMap.put("describe", judgeDatabase.getDescribe());
		
		return modelMap;
	}

	/* 查询教师用户的全部答题数据库信息 */
	@Override
	public List<Map<String, Object>> getJudgeDatabasesByteacherId(String userId) {
		List<JudgeDatabase> judgeDatabases = judgeDatabaseMapper.getJudgeDatabasesByteacherId(userId);
		List<Map<String, Object>> result = new ArrayList<>();
		
		for(JudgeDatabase judgeDatabase : judgeDatabases) {
			Map<String, Object> modelMap = new HashMap<>();
			
			modelMap.put("name", judgeDatabase.getName());
			modelMap.put("describe", judgeDatabase.getDescribe());
			
			result.add(modelMap);
		}
		
		return result;
	}

	/* 删除答题数据库信息 */
	@Override
	public Map<String, Object> deleteJudgeDatabaseByName(String name) {
		Map<String,Object> modelMap = new HashMap<String, Object>();
		//判断答题数据库是否存在
		QueryWrapper<JudgeDatabase> judgeDatabaseWrapper = new QueryWrapper<>();
		judgeDatabaseWrapper.eq("name", name);
		JudgeDatabase judgeatabase = judgeDatabaseMapper.selectOne(judgeDatabaseWrapper);
		
		if (judgeatabase == null) {
			log.info("111111");
			modelMap.put("state", "error");
        	modelMap.put("errmessage", "答题数据库不存在");
        	return modelMap;
		}
		
		//判断是否存在题目未删除，存在无法删除或转换该答题数据库
		QueryWrapper<Question> questionWrapper = new QueryWrapper<>();
		questionWrapper.eq("dbname", name);
		int questionCount = questionMapper.selectCount(questionWrapper);
		if (questionCount > 0) {
			log.info("222222");
			modelMap.put("state", "error");
			modelMap.put("errmessage", "答题数据库中存在相应题目，无法被删除或被转为实践数据库");
			return modelMap;
		}
		
		//删除答题数据库表内信息
		int mes = judgeDatabaseMapper.delete(judgeDatabaseWrapper);
		if(mes==1){
            modelMap.put("state","success");
        }
        else {
			log.info("333333");
            modelMap.put("state","error");
            modelMap.put("errmessage","删除失败，请联系数据库管理员解决");
        }
		
		return modelMap;
		
	}
	

}
