package com.example.ex02.mapper;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.ex02.dto.MemberDto;

@Mapper
public interface MemberMapper {
	MemberDto findById(String id);
	
	MemberDto findByPassword(String password);
	
	int insert(MemberDto dto);

	int getVisit(LocalDate visitDate);

	void insertVisit(LocalDate visitDate);
	
	void updateVisit(LocalDate visitDate);
}
