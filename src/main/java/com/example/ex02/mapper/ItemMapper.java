package com.example.ex02.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.ex02.dto.ItemDto;
import com.example.ex02.util.CriteriaDto;

@Mapper
public interface ItemMapper {
	List<ItemDto> getList(CriteriaDto dto);
	
	List<ItemDto> list();
	
	ItemDto get(Long ino);

	int getCount(CriteriaDto dto);
	
	int getAmountCount(Long ino);
	
	void insert(ItemDto dto);

	void update(ItemDto dto);
	
	void delete(Long ino);

	void amountUpdate(@Param("ino") Long ino, @Param("amount") int amount);
}
