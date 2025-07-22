package com.example.ex02.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.ex02.dto.BoardDto;
import com.example.ex02.util.CriteriaDto;
import com.example.ex02.util.PageDto;

@Mapper
public interface BoardMapper {
	List<Map<String, Object>> getList(CriteriaDto dto);

	BoardDto get(Long bno);
	
	int insert(BoardDto dto);
	
	int update(BoardDto dto);
	
	int delete(Long bno);

	int getTotalCount(CriteriaDto dto);

	int viewCountupdate(Long bno);
	
	int replyCountupdate(@Param("bno")Long bno, @Param("count")int count);
}
