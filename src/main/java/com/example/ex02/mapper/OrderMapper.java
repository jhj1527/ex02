package com.example.ex02.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.ex02.dto.OrderDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;

@Mapper
public interface OrderMapper {
	List<OrderDto> getList(String id);
	
	OrderDto getDetailList(String orderId);
	
	void insert(OrderDto dto);

	void orderItemInsert(List<OrderItemDto> list);

	void update(OrderDto dto);
	
	void updateState(@Param("oino") Long oino, @Param("state") int state);
	
	OrderItemDto getOrderItemByOino(Long oino);

	void updatePrice(Map<String, Object> map);

}
