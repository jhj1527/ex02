package com.example.ex02.service;

import java.util.List;
import java.util.Map;

import com.example.ex02.dto.BoardDto;
import com.example.ex02.util.CriteriaDto;
import com.example.ex02.util.PageDto;

public interface BoardService {
	BoardDto get(Long bno);
	
	int insert(BoardDto dto);
	
	int update(BoardDto dto, Long bno);
	
	int delete(Long bno);

	int getTotalCount(CriteriaDto dto);

	List<Map<String, Object>> getList(CriteriaDto dto);

	int viewCountupdate(Long bno);
}
