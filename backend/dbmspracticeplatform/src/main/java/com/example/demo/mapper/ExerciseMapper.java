package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Exercise;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExerciseMapper extends BaseMapper<Exercise> {
    List<Exercise> getALLExercise(String id) ;
    
    List<Exercise> getExercises();
    
    //获取其他教师使用该试卷的数量
    int getOtherTeacherUseCount(int exerciseId);

}
