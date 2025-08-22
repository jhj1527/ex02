package com.example.ex02.service;

import java.time.LocalDate;

import com.example.ex02.dto.MemberDto;

public interface MemberService {
	MemberDto findById(String id);
	
	MemberDto findByPassword(String password);
	
	int insert(MemberDto dto);
	
	int getVisit(LocalDate visitDate);

	void insertVisit(LocalDate visitDate);
	
	void updateVisit(LocalDate visitDate);
}
