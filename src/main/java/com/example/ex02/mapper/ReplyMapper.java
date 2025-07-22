package com.example.ex02.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.example.ex02.dto.ReplyDto;

@Mapper
public interface ReplyMapper {
	// mybatis에서 두 개 이상 파라미터 전달시 3가지 방법
	// 1.별도의 객체로 구현
	// 2.map을 이용하여 처리
	// 3.@Param 어노테이션을 이용하여 처리
	
//	List<ReplyDto> getList(@Param("dto")PageDto dto, @Param("bno")Long bno);

	List<ReplyDto> getList(Map<String, Object> map);
	
	int getTotalCount(Long bno);
	
	ReplyDto get(Long rno);

	int insert(ReplyDto dto);

	int update(ReplyDto dto);

	int delete(Long rno);

	List<ReplyDto> getListByBno(Long bno);
}
