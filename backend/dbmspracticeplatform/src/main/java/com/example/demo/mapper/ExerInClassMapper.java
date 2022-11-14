package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.ExerInClass1;
import org.apache.ibatis.annotations.Mapper;
import com.example.demo.entity.ExerInClass;
import com.example.demo.entity.SQLResult;

import java.util.List;

@Mapper
public interface ExerInClassMapper extends BaseMapper<ExerInClass> {
    /*查询某班级的全部试卷*/
    List<ExerInClass> getClassALLExer(String stuClassId);

    /*查询某班级的全部试卷包括得分情况*/
    List<ExerInClass1> getClassALLExers(String stuClassId,String stuId);
    
    /*查询某班级的可作答的全部试卷*/
    List<ExerInClass> getClassALLExerInTime(String stuClassId);

    /*查询某班级的可作答的全部试卷包括得分情况*/
    List<ExerInClass1> getClassALLExersInTime(String stuClassId, String stuId);

    /* 查询某试卷所使用的班级 */
    List<SQLResult> getClassByExerciseId(int exerciseId);
    
    /* 查询教师发布的所有班级试卷关系*/
    List<SQLResult> getALLExerInClassByTeacherId(String teacherId);
}
