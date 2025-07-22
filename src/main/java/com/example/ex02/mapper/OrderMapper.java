package com.example.ex02.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.ex02.dto.OrderDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;

@Mapper
public interface OrderMapper {
	List<OrderDto> getList(String id);
	
	OrderDto getDetailList(String orderId);
	
	OrderDto get(String orderId);
	
	void insert(OrderDto dto);

	void orderItemInsert(List<OrderItemDto> list);

	void update(OrderDto dto);
	
	void delete(String orderId);
	
	void orderItemDelete(String orderId);
}
