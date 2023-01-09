package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.JudgeDatabase;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JudgeDatabaseMapper extends BaseMapper<JudgeDatabase> {
	
	List<JudgeDatabase> getJudgeDatabasesByteacherId(String userId);
}
