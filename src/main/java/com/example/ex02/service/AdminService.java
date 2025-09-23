package com.example.ex02.service;

import java.util.List;
import java.util.Map;

import com.example.ex02.dto.BarChartDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;
import com.example.ex02.dto.PieChartDto;

public interface AdminService {
	Map<String, Object> ChartList();
	
	List<BarChartDto> barChartList();
	
	List<PieChartDto> pieChartList();
	
	Map<String, Object> dashBoard();
	
	List<Map<String, Object>> memberList();
	
	List<OrderItemDto> orderList();
}
