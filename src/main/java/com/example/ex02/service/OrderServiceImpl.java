package com.example.ex02.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ex02.dto.CartDto;
import com.example.ex02.dto.OrderDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;
import com.example.ex02.mapper.CartMapper;
import com.example.ex02.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
	private final OrderMapper orderMapper;
	private final CartMapper cartMapper;
	
	@Autowired
	public OrderServiceImpl(OrderMapper orderMapper, CartMapper cartMapper) {
		this.orderMapper = orderMapper;
		this.cartMapper = cartMapper;
	}
	
	@Override
	public List<OrderDto> getList(String id) {
		return orderMapper.getList(id); 
	}
	
	@Override
	public OrderDto getDetailList(String orderId) {
		return orderMapper.getDetailList(orderId);
	}

	@Transactional
	@Override
	public void insert(OrderDto dto) {
//		dto.setOrderId(craeteNum(6));
		
		dto.getList().stream().forEach(order -> {
			order.setOrderId(dto.getOrderId());
			order.setImp_uid(dto.getImp_uid());
			
			CartDto cartDto = CartDto
					.builder()
					.id(order.getId())
					.ino(order.getIno())
					.build();
			
			cartDto = cartMapper.get(cartDto);
			
			if (cartDto != null) {
				cartMapper.delete(cartDto.getCno());
			}
		});
		
		orderMapper.insert(dto);
		
		orderMapper.orderItemInsert(dto.getList());
	}
	
	@Transactional
	@Override
	public void update(OrderDto dto) {
		orderMapper.update(dto);
	}
	
	@Transactional
	@Override
	public void cancel(Long oino) {
		OrderItemDto orderItemDto = orderMapper.getOrderItemByOino(oino);
		
		int price = 0;
		
		OrderDto dto = getDetailList(orderItemDto.getOrderId());
		price = orderItemDto.getPrice() * orderItemDto.getQuantity();
		
		List<OrderItemDto> list = dto.getList()
				.stream()
				.filter(item -> item.getState() != 4)
				.collect(Collectors.toList());
		
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", orderItemDto.getOrderId());
		map.put("price", price);
		
		if (list.size() == 1) {
			map.put("charge", dto.getCharge());
			
		} else {
			map.put("charge", 0);
		}
		
		orderMapper.updateState(oino, 4);
		
		orderMapper.updatePrice(map);
	}
	
	@Override
	public String craeteNum(int range) {
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formatDate = today.format(formatter);
		
        Random ramdom = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(formatDate);

        IntStream.range(0 , range).forEach(i -> {
            stringBuilder.append(ramdom.nextInt(10));
        });
        
        log.info("orderId : " + stringBuilder.toString());

        return stringBuilder.toString();
	}
}
