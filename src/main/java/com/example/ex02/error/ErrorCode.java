package com.example.ex02.error;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Getter
public enum ErrorCode {
	MEMBER_NOTFOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "아이디 미존재"),
	DUPLICATION(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value()),
	MEMBER_REQUIRED(HttpStatus.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST),
	BAD_REQUEST(HttpStatus.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
	EXCEED_CART(HttpStatus.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST, "수량초과");
	
	private HttpStatus type;
	private int code;
	private String message;
	
	private ErrorCode(HttpStatus type, int code) {
		this.type = type;
		this.code = code;
	}

	private ErrorCode(HttpStatus type, int code, String message) {
		this.type = type;
		this.code = code;
		this.message = message;
	}
}
