package com.example.ex02.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.ex02.dto.CartDto;

@Mapper
public interface CartMapper {
	List<CartDto> getList(String id);
	
	List<CartDto> getCheckList(int[] checkArr);
	
	CartDto getByCno(Long cno);
	
	CartDto get(CartDto dto);
	
	int getCount(String id);
	
	int[] totalPrice(String id);
	
	void insert(CartDto dto);
	
	void update(CartDto dto);
	
	void updateList(List<CartDto> list);
	
	void delete(Long cno);
}
