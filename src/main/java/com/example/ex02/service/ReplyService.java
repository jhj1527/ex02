package com.example.ex02.service;

import java.util.List;
import java.util.Map;

import com.example.ex02.dto.ReplyDto;

public interface ReplyService {
	List<ReplyDto> getList(Map<String, Object> map);
	
	int getTotalCount(Long bno);

	ReplyDto get(Long rno);

	int insert(ReplyDto dto);

	boolean update(ReplyDto dto);

	boolean delete(Long rno);

}
