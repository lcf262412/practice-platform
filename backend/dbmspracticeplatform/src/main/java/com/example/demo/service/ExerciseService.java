package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Exercise;

import java.util.List;
import java.util.Map;

public interface ExerciseService extends IService<Exercise> {

    /*增加试卷*/
    Map<String,Object> addExercise( String teacherId, String name, String describe);

    /*逻辑删除试卷*/
    Map<String,Object> delExercise( int id);

    /*修改试卷信息*/
    Map<String,Object> modifyExercise( int id, String name, String describe);

    /*查询全部可使用试卷*/
    List<Map<String,Object>> getAllExercise(String id);

    /*设置试卷开放*/
    Map<String,Object> setExercisePublic( int id);

    /*设置试卷私用*/
    Map<String,Object> setExercisePrivate( int id);
    
    /* 物理删除试卷 */
    Map<String, Object> delExercisePhysically( int id);
    
    /* 查询全部试卷（包括已被删除试卷） */
    List<Map<String, Object>> getExercises();
}
