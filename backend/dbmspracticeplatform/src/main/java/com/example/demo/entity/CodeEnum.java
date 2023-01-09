package com.example.demo.entity;

public enum CodeEnum {
	
	/* 成功 */
	SUCCESS("0000","success"),
	
	/* 失败 */
	ERROR("1111","fail");
	
	private String code;
	
	private String message;
	
	CodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}

}
