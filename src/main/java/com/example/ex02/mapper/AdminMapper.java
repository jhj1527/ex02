package com.example.ex02.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.ex02.dto.BarChartDto;
import com.example.ex02.dto.PieChartDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;

@Mapper
public interface AdminMapper {
	List<BarChartDto> barChartList();
	
	List<PieChartDto> pieChartList();

	Map<String, Object> dashBoard();

	List<Map<String, Object>> memberList();
	
	List<OrderItemDto> orderList();
}
