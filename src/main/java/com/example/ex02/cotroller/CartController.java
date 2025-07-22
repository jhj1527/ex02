package com.example.ex02.cotroller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ex02.dto.CartDto;
import com.example.ex02.exception.ApiException;
import com.example.ex02.service.CartService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {
	private final CartService cartService;
	
	@Autowired
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> getList(@RequestParam("id") String id) {
		int totalPrice = 0;
		
		List<CartDto> list = cartService.getList(id);
		
		if (list != null && !list.isEmpty()) {
			totalPrice = Arrays.stream(cartService.totalPrice(id)).sum();
		}
		
//		log.info("totalPrice : " + totalPrice);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("list", list);
		map.put("totalPrice", totalPrice);
		
		return ResponseEntity.ok(list);
	}
	
	@GetMapping("/checkList")
	public ResponseEntity<?> getCheckList(@RequestParam("checkArr") int[] checkArr) {
		List<CartDto> list = cartService.getCheckList(checkArr);
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@GetMapping("/getCount")
	public ResponseEntity<?> getCount(@RequestParam("id") String id) {
		int count = cartService.getCount(id);
		
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
	
	@PostMapping("/insert")
	public ResponseEntity<?> insert(@RequestBody CartDto dto, HttpServletResponse response) throws URISyntaxException {
		try {
			cartService.insert(dto);
			
			return new ResponseEntity<>("insert", HttpStatus.CREATED);
			
		} catch (ApiException e) {
			log.error(e.getMessage());
			
			if (e.getMessage().equals("수량초과")) {
				return new ResponseEntity<>(e.getMessage(), e.getError().getType());
				
			} else {
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setLocation(new URI("/api/cart/update"));
				
				return new ResponseEntity<>(httpHeaders, HttpStatus.PERMANENT_REDIRECT);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return errorResponse(e);
		}
	}
	
	@PostMapping("/update")
	public ResponseEntity<?> update(@RequestBody CartDto dto) {
		try {
			cartService.update(dto);
			
			return new ResponseEntity<>("update", HttpStatus.OK);
			
		} catch (ApiException e) {
			return ApiErrorResponse(e);
			
		} catch (Exception e) {
			return errorResponse(e);
		}
	}
	
	@PatchMapping("/updateList")
	public ResponseEntity<?> updateList(@RequestBody List<CartDto> list) {
		try {
//			list.stream().forEach(c -> {
//				log.info(c.toString());
//			});
			
			cartService.updateList(list);
			
			return new ResponseEntity<>("updateList", HttpStatus.OK);
			
		} catch (ApiException e) {
			return ApiErrorResponse(e);
			
		} catch (Exception e) {
			return errorResponse(e);
		}
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(@RequestParam("cno") Long cno) {
		try {
			cartService.delete(cno);
			
			return new ResponseEntity<>("delete", HttpStatus.OK);
			
		} catch (ApiException e) {
			return ApiErrorResponse(e);
			
		} catch (Exception e) {
			return errorResponse(e);
		}
	}
	
	public ResponseEntity<?> ApiErrorResponse(ApiException e) {
		Map<String, Object> map = new HashMap<>();
		map.put("message", e.getError().getMessage());
		
		return ResponseEntity.internalServerError().body(map);
	}
	
	public ResponseEntity<?> errorResponse(Exception e) {
		Map<String, Object> map = new HashMap<>();
		map.put("message", e.getMessage());
		
		return ResponseEntity.internalServerError().body(map);
	}
}
