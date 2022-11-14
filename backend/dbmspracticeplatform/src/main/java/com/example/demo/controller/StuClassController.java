package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.BaseResponse;
import com.example.demo.service.StuClassService;

@RequestMapping("/stuclass")
@CrossOrigin
@RestController
public class StuClassController {
	@Autowired
	private StuClassService stuClassService;
	
	/* 增加班级接口 */
	@PostMapping(value = "/addStuClass")
	public BaseResponse<Map<String, Object>> addStuClass(@RequestParam(value = "id") String id,
			                                             @RequestParam(value = "teacherId") String teacherId,
			                                             @RequestParam(value = "semester") String semester) {
		Map<String,Object> result = stuClassService.insertStuClass(id, teacherId, semester);
		
		if("error".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
	}
	
	/* 根据教师编号查询班级列表接口 */
	@GetMapping(value = "/getStuClassByTeacherId")
	public BaseResponse<List<Map<String, Object>>> getStuClassByteacherId(@RequestParam(value = "teacherId") String teacherId) {
		return BaseResponse.success(stuClassService.getStuClassByteacherId(teacherId));
	}
	
	/* 删除班级接口 */
	@PostMapping(value = "/deleteStuClass")
	public BaseResponse<Map<String, Object>> deleteStuClass(@RequestParam(value = "id") String id) {
		Map<String, Object> result = stuClassService.deleteStuClass(id);
		if("error".equals(result.get("state"))){
            return BaseResponse.fail(result);
        }
        return  BaseResponse.success(result);
	}

}
