package com.example.ex02.cotroller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ex02.dto.ItemDto;
import com.example.ex02.service.AdminService;
import com.example.ex02.service.ItemService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {
	private final AdminService adminService;
	private final ItemService itemService;
	
	@Autowired
	public AdminController(AdminService adminService, ItemService itemService) {
		this.adminService = adminService;
		this.itemService = itemService;
	}
	
	@GetMapping("/chart")
	public ResponseEntity<?> chartList() {
		Map<String, Object> map = adminService.ChartList();
		
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@GetMapping("/item")
	public ResponseEntity<?> itemList() {
		List<ItemDto> list = itemService.list();
		
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
}
