package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.ExerInClass;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ExerInClassService extends IService<ExerInClass> {
    /*增加班级与试卷关系*/
    Map<String,Object> addExerInClass(String stuClassId, int exerciseId, boolean isTest, Date startTime,Date endTime);

    /*修改班级与试卷关系*/
    Map<String,Object> modifyExerInClass(String stuClassId, int exerciseId, boolean isTest, Date startTime,Date endTime);
    
    /*删除班级与试卷关系*/
    Map<String,Object> delExerInClass(String stuClassId, int exerciseId);
    
    /*删除指定班级的全部试卷*/
    Map<String,Object> delExersByClassId(String stuClassId);

    /*查询某班级的全部试卷*/
    List<Map<String,Object>> getClassAllExer(String stuClassId, boolean showAll);

    /*查询某班级的全部试卷包括试卷得分*/
    List<Map<String,Object>> getClassAllExers(String stuClassId, String stuId,boolean showAll);

    /*查询使用某试卷的班级*/
    List<Map<String,Object>> getExerUsedClass(int exerciseId, String teacherId);
    
    /*查询教师发布的所有班级试卷关系*/
    List<Map<String,Object>> getALLExerInClassByTeacherId(String teacherId);

    /*查询指定试卷是否被使用*/
    boolean exerIsUsed(int exerciseId);

}
