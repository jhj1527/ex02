package com.example.ex02.cotroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ex02.dto.OrderDto;
import com.example.ex02.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {
	private final OrderService orderService;
	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> getList(@RequestParam("id") String id) {
		List<OrderDto> list = orderService.getList(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	@GetMapping("/detailList/{orderId}")
	public ResponseEntity<?> getDetailList(@PathVariable("orderId") String orderId) {
		OrderDto dto = orderService.getDetailList(orderId);
		
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
	
	@GetMapping("/getOrderId")
	public ResponseEntity<?> getOrderId() {
		String orderId = orderService.craeteNum(6);
		
		return ResponseEntity.status(HttpStatus.OK).body(orderId);
	}

	@PostMapping("/insert")
	public ResponseEntity<?> OrderIsert(@RequestBody OrderDto dto) {
		orderService.insert(dto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("insert");
	}
	
	@PatchMapping("/update")
	public ResponseEntity<?> update(@RequestBody OrderDto dto) {
		orderService.update(dto);
		
		return ResponseEntity.status(HttpStatus.OK).body("update");
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(@RequestParam("orderId") String orderId) {
		orderService.delete(orderId);
		
		return ResponseEntity.status(HttpStatus.OK).body("delete");
	}
	
}
