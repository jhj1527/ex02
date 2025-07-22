package com.example.ex02.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.ex02.dto.MemberDto;

@Mapper
public interface MemberMapper {
	MemberDto findById(String id);
	
	MemberDto findByPassword(String password);
	
	int insert(MemberDto dto);
}
