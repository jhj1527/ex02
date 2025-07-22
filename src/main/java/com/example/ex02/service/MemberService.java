package com.example.ex02.service;

import com.example.ex02.dto.MemberDto;

public interface MemberService {
	MemberDto findById(String id);
	
	MemberDto findByPassword(String password);
	
	int insert(MemberDto dto);
}
