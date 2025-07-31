package com.example.ex02.cotroller;

import java.util.HashMap;
import java.util.Map;

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

import com.example.ex02.dto.ItemDto;
import com.example.ex02.exception.ApiException;
import com.example.ex02.service.ItemService;
import com.example.ex02.util.CriteriaDto;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/item")
@Slf4j
public class ItemController {
	private final ItemService itemService;
	
	@Autowired
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> getList(@RequestParam(name = "pageNum") int pageNum, @RequestParam(name = "amount") int amount, 
			@RequestParam(name = "type") String type, @RequestParam(name = "keyword") String keyword) {
		
		CriteriaDto dto = CriteriaDto.builder()
				.pageNum(pageNum)
				.amount(amount)
				.type(type)
				.keyword(keyword)
				.build();
		
//		log.info(dto.toString());
		
		return ResponseEntity.ok().body(itemService.getList(dto));
	}
	
	@GetMapping("/{ino}")
	public ResponseEntity<?> get(@PathVariable("ino") Long ino) {
		ItemDto dto = itemService.get(ino);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@PostMapping("/insert")
	public ResponseEntity<?> insert(@RequestBody ItemDto dto) throws Exception {
		try {
			itemService.insert(dto);
			
		} catch (ApiException e) {
			log.error("ApiException ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶");
			Map<String, Object> map = new HashMap<>();
			
			map.put("message", e.getMessage());
			
			return ResponseEntity.status(e.getError().getType()).body(map);
		} 
		
		return new ResponseEntity<>("insert", HttpStatus.CREATED);
	}
	
	@PatchMapping("/update")
	public ResponseEntity<?> update(@RequestBody ItemDto dto) {
		try {
			itemService.update(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			Map<String, Object> map = new HashMap<>();
			map.put("message", e.getMessage());
			
			log.error(map.get("message").toString());
			
			return ResponseEntity.internalServerError().body(map);
		}
		
		return new ResponseEntity<>("ok", HttpStatus.OK);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(@RequestParam("ino") Long ino) {
		try {
			itemService.delete(ino);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			Map<String, Object> map = new HashMap<>();
			map.put("message", e.getMessage());
			
			log.error(map.get("message").toString());
			
			return ResponseEntity.internalServerError().body(map);
		}
		
		return new ResponseEntity<>("ok", HttpStatus.OK);
	}
}
