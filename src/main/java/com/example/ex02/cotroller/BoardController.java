package com.example.ex02.cotroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ex02.dto.BoardDto;
import com.example.ex02.exception.ApiException;
import com.example.ex02.service.BoardService;
import com.example.ex02.util.CriteriaDto;
import com.example.ex02.util.PageDto;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/board")
//@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class BoardController {
private final BoardService boardService;
	
	@Autowired
	public BoardController(BoardService boardService) {
		this.boardService = boardService;
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
		
		int count = boardService.getTotalCount(dto);
		
		PageDto pageDto = new PageDto(dto, count);
		
		List<Map<String, Object>> list = boardService.getList(dto);
		Map<String, Object> map = new HashMap<>();
		
		map.put("list", list);
		map.put("page", pageDto);
		
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
		
	@GetMapping("/{bno}")
	public ResponseEntity<?> get(@PathVariable("bno") Long bno) { // SpringBoot 3.2 버전 이상부터 매개변수를 명시 해주지 않으면 오류 발생
		try {
			BoardDto dto = boardService.get(bno);
			
			return ResponseEntity.status(HttpStatus.OK).body(dto);
			
		} catch (ApiException e) {
			Map<String, Object> map = new HashMap<>();
			log.error("error!!!!!!!!!!!!!!!!!!!");
			
			map.put("message", e.getMessage());
			
			return ResponseEntity.status(e.getStatus()).body(map);
		}
		
//		return dto != null ? ResponseEntity.status(HttpStatus.OK).body(dto) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MEMBER')")
	@PostMapping("/insert")
	public ResponseEntity<?> insert(@RequestBody BoardDto dto) {
		int result = boardService.insert(dto);
		
		return result == 1 ? ResponseEntity.status(HttpStatus.OK).body(result) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
	
	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/{bno}")
	public ResponseEntity<?> update(@RequestBody BoardDto dto, @PathVariable("bno") Long bno) {
		int result = boardService.update(dto, bno);
			
		return result == 1 ? ResponseEntity.status(HttpStatus.OK).body(result) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{bno}")
	public ResponseEntity<?> delete(@PathVariable("bno") Long bno) {
		int result = boardService.delete(bno);
					
		return result == 1 ? ResponseEntity.status(HttpStatus.OK).body(result) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
}
