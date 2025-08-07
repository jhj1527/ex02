package com.example.ex02.service;

import java.util.List;

import com.example.ex02.dto.OrderDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;

public interface OrderService {
	List<OrderDto> getList(String id);
	
	OrderDto getDetailList(String orderId);
	
	void insert(OrderDto dto);

	void cancel(Long oino);

	void update(OrderDto dto);
	
	String craeteNum(int range);
}
