package com.example.ex02.exception;

import org.springframework.http.HttpStatus;

import com.example.ex02.error.ErrorCode;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private ErrorCode error;
	private String message;
	private HttpStatus status;

	public ApiException(ErrorCode error, String message) {
		super();
		this.error = error;
		this.message = message;
	}
	
	public ApiException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public ApiException(ErrorCode error) {
		super();
		this.error = error;
	}
}
