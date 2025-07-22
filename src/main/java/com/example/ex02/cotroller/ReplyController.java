package com.example.ex02.cotroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ex02.dto.ReplyDto;
import com.example.ex02.exception.ApiException;
import com.example.ex02.service.ReplyService;
import com.example.ex02.util.CriteriaDto;
import com.example.ex02.util.PageDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reply")
public class ReplyController {
	
	@Autowired
	public ReplyController(ReplyService replyService) {
		this.replyService = replyService;
	}

	private final ReplyService replyService;
	
	@GetMapping("/list/{bno}/{pageNum}")
	public ResponseEntity<?> getList(@PathVariable("bno") Long bno, @PathVariable("pageNum") int pageNum) {
		CriteriaDto criteriaDto = CriteriaDto.builder()
				.pageNum(pageNum)
				.amount(5)
				.build();
		
		int count = replyService.getTotalCount(bno);
		PageDto pageDto = new PageDto(criteriaDto, count);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("bno", bno);
		map.put("dto", criteriaDto);
		
		List<ReplyDto> list = replyService.getList(map);
		
		map.clear();
		map.put("page", pageDto);
		map.put("list", list);
		
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@GetMapping("/{rno}")
	public ResponseEntity<?> get(@PathVariable("rno") Long rno) {
		try {
			ReplyDto dto = replyService.get(rno);
			
			return ResponseEntity.status(HttpStatus.OK).body(dto);
			
		} catch (ApiException e) {
			Map<String, Object> map = new HashMap<>();
			log.error("error!!!!!!!!!!!!!!!!!!!");
			
			map.put("message", e.getMessage());
			
			return ResponseEntity.status(e.getStatus()).body(map);
		}
	}
	
	@PostMapping("/insert")
	public ResponseEntity<?> insert(@RequestBody ReplyDto dto) {
		try {
			int result = replyService.insert(dto);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(result);
			
		} catch (ApiException e) {
			Map<String, Object> map = new HashMap<>();
			log.error("error!!!!!!!!!!!!!!!!!!!");
			
			map.put("error", e.getError());
			
			return ResponseEntity.status(e.getError().getType()).body(map);
		}
	}
	
	@PreAuthorize("principal.memberDto.id == #dto.id")
	@PatchMapping("/update")
	public ResponseEntity<?> update(@RequestBody ReplyDto dto, Authentication auth) {
		try {
			boolean result = replyService.update(dto);
			
			return ResponseEntity.status(HttpStatus.OK).body(result);
			
		} catch (ApiException e) {
			Map<String, Object> map = new HashMap<>();
			log.error("error!!!!!!!!!!!!!!!!!!!");
			
			map.put("error", e.getError());
			
			return ResponseEntity.status(e.getError().getType()).body(map);
		}
	}
	
	@DeleteMapping("/delete/{rno}")
	public ResponseEntity<?> delete(@PathVariable("rno") Long rno) {
		try {
			boolean result = replyService.delete(rno);
			
			return ResponseEntity.status(HttpStatus.OK).body(result);
			
		} catch (ApiException e) {
			Map<String, Object> map = new HashMap<>();
			log.error("error!!!!!!!!!!!!!!!!!!!");
			
			map.put("error", e.getError());
			
			return ResponseEntity.status(e.getError().getType()).body(map);
		}
	}
	
	
}
