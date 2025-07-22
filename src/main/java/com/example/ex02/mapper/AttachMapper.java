package com.example.ex02.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.ex02.dto.AttachDto;

@Mapper
public interface AttachMapper {
	List<AttachDto> getList(Long ino);
	
	void insert(AttachDto dto);
	
	void delete(String attachId);
	
	void deleteAll(Long ino);
}
