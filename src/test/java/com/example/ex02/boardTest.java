package com.example.ex02;

import java.time.LocalDate;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.ex02.config.DatabaseConfig;
import com.example.ex02.dto.BoardDto;
import com.example.ex02.mapper.BoardMapper;
import com.example.ex02.service.BoardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import({DatabaseConfig.class})
//@ContextConfiguration(classes = {DatabaseConfig.class , BoardServiceImpl.class})
@SpringBootTest
//@MybatisTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class boardTest {
	@Autowired
	private BoardService boardService;
	@Autowired
	private BoardMapper boardMapper;

	@Test
	void contextLoads() {
//		List<Map<String , Object>> list = boardService.getList();
		
//		log.info("list : " + list.toString());
		
		BoardDto dto = new BoardDto();
		
		LongStream.rangeClosed(16, 150).forEach(i -> {
			dto.setBno(i);
			dto.setTitle("title" + i);
			dto.setContent("content" + i);
			dto.setId("member1");
//			dto.setRegDate(LocalDate.now());
			
			boardService.insert(dto);
		});
		
		
	}

}
