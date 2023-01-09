package com.example.demo.entity;

import lombok.Data;

@Data
public class BaseResponse<T> {
	
	private String code;
	
	private String message;
	
	private T result;
	
	/* 默认成功 */
	public static <T> BaseResponse<T> success(T result) {
		BaseResponse<T> response = new BaseResponse<>();
		response.setCode(CodeEnum.SUCCESS.getCode());
		response.setMessage(CodeEnum.SUCCESS.getMessage());
		response.setResult(result);
		return response;
	}
	
	/* 默认失败 */
	public static <T> BaseResponse<T> fail() {
		BaseResponse<T> response = new BaseResponse<>();
		response.setCode(CodeEnum.ERROR.getCode());
		response.setMessage(CodeEnum.ERROR.getMessage());
		return response;
	}
	
	/* 默认失败 带失败数据 */
	public static <T> BaseResponse<T> fail(T result) {
		BaseResponse<T> response = new BaseResponse<>();
		response.setCode(CodeEnum.ERROR.getCode());
		response.setMessage(CodeEnum.ERROR.getMessage());
		response.setResult(result);
		return response;
	}
	
	/* 自定义失败状态码和结果描述 */
	public static <T> BaseResponse<T> fail(String code, String message) {
		BaseResponse<T> response = new BaseResponse<>();
		response.setCode(code);
		response.setMessage(message);
		return response;
	}
	
	/* 自定义失败状态码、结果描述和返回数据 */
	public static <T> BaseResponse<T> fail(String code, String message, T result) {
		BaseResponse<T> response = new BaseResponse<>();
		response.setCode(code);
		response.setMessage(message);
		response.setResult(result);
		return response;
	}
	

}
