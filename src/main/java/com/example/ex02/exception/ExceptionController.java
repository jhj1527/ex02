package com.example.ex02.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<?> errorHandler(ApiException e, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
				
		map.put("type", e.getError().getType());
		map.put("code", e.getError().getCode());
		map.put("message", e.getError().getMessage());
		
		return new ResponseEntity<>(map, httpStatus);
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	}
}
