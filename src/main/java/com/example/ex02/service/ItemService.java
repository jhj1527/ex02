package com.example.ex02.service;

import java.util.Map;


import com.example.ex02.dto.ItemDto;
import com.example.ex02.util.CriteriaDto;

public interface ItemService {
	Map<String, Object> getList(CriteriaDto dto);
	
	ItemDto get(Long ino);
	
	int getCount(CriteriaDto dto);
	
	void insert(ItemDto dto) throws Exception;

	void update(ItemDto dto);
	
	void delete(Long ino);
}
