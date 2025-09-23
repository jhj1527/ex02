package com.example.ex02.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ex02.dto.BarChartDto;
import com.example.ex02.dto.PieChartDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;
import com.example.ex02.mapper.AdminMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
	private final AdminMapper adminMapper;
	
	@Autowired
	public AdminServiceImpl(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
	}
	
	@Override
	public Map<String, Object> ChartList() {
		Map<String, Object> map = new HashMap<>();

		map.put("barChart", barChartList());
		map.put("pieChart", pieChartList());
		map.put("dashboard", dashBoard());
		map.put("member", memberList());
		map.put("order", orderList());
		
		return map;
	}

	@Override
	public List<BarChartDto> barChartList() {
		List<BarChartDto> list = adminMapper.barChartList();
		return list;
	}

	@Override
	public List<PieChartDto> pieChartList() {
		List<PieChartDto> list = adminMapper.pieChartList()
				.stream()
				.filter(pie -> pie.getCategory() != null)
				.collect(Collectors.toList());;
		
		return list;
	}

	@Override
	public Map<String, Object> dashBoard() {
		return adminMapper.dashBoard();
	}

	@Override
	public List<Map<String, Object>> memberList() {
		return adminMapper.memberList();
	}

	@Override
	public List<OrderItemDto> orderList() {
		return adminMapper.orderList();
	}
}
