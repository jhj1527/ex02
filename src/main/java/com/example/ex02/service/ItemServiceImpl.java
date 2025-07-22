package com.example.ex02.service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ex02.dto.AttachDto;

import com.example.ex02.dto.ItemDto;

import com.example.ex02.error.ErrorCode;
import com.example.ex02.exception.ApiException;
import com.example.ex02.mapper.AttachMapper;
import com.example.ex02.mapper.ItemMapper;
import com.example.ex02.util.CriteriaDto;
import com.example.ex02.util.PageDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
	private final ItemMapper itemMapper;
	private final AttachMapper attachMapper;
	
	@Autowired
	public ItemServiceImpl(ItemMapper itemMapper, AttachMapper attachMapper) {
		this.itemMapper = itemMapper;
		this.attachMapper = attachMapper;
	}
	
	@Override
	public Map<String, Object> getList(CriteriaDto dto) {
		int count = getCount(dto);
		
		PageDto pageDto = new PageDto(dto, count);
		
		List<ItemDto> list = itemMapper.getList(dto);
		
		for (ItemDto itemDto : list) {
			List<AttachDto> AttachList = attachMapper.getList(itemDto.getIno());
			itemDto.setAttachList(AttachList);
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("page", pageDto);
		
		return map;
	}
	
	@Override
	public ItemDto get(Long ino) {
		ItemDto dto = itemMapper.get(ino);
		
		if (dto != null) {
			List<AttachDto> list = attachMapper.getList(dto.getIno())
					.stream()
//					.filter(a -> a.isFileType())
					.collect(Collectors.toList());
			
			dto.setAttachList(list);
		}
		
		return dto;
	}

	@Override
	public int getCount(CriteriaDto dto) {
		return itemMapper.getCount(dto);
	}
	
	@Override
	@Transactional
	public void insert(ItemDto dto) {
		try {
			itemMapper.insert(dto);
			
			log.info(dto.toString());
			
			if (!dto.getAttachList().isEmpty() && dto.getAttachList() != null) {
				dto.getAttachList().forEach(attach -> {
					attach.setIno(dto.getIno());
					attachMapper.insert(attach);
				});
			}
			
		} catch (Exception e) {
			ExceptionHandeler(e);
		}
	}
	
	@Override
	@Transactional
	public void update(ItemDto dto) {
		try {
			attachMapper.deleteAll(dto.getIno());
			
			itemMapper.update(dto);
			
			if (dto.getAttachList() != null && !dto.getAttachList().isEmpty()) {
				dto.getAttachList().forEach(attach -> {
					attach.setIno(dto.getIno());
					attachMapper.insert(attach);
				});
			}
			
		} catch (Exception e) {
			ExceptionHandeler(e);
		}
	}
	
	@Override
	@Transactional
	public void delete(Long ino) {
		try {
			List<AttachDto> list = attachMapper.getList(ino);
			
			itemMapper.delete(ino);
			
			attachMapper.deleteAll(ino);
			
			deleteFiles(list);
            
		} catch (Exception e) {
			ExceptionHandeler(e);
		}
	}
	
	public void deleteFiles(List<AttachDto> list) {
        log.info("deleteFiles ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ :  : " + list);

        if (list == null || list.isEmpty()) return;
        
        String uploadFolder = "D:\\upload";
        
        list.stream().forEach(dto -> {
        	try {
                Path path = Paths.get(uploadFolder + "\\" + dto.getFilePath().trim() + "\\" 
                		+ URLDecoder.decode(dto.getFileName(), "UTF-8"));

                Files.deleteIfExists(path);
                
                log.info(String.valueOf(Files.list(path.getParent()).count()));
                
                if (Files.list(path.getParent()).count() == 0) {
                	Files.delete(path.getParent()); 
                }
                
            }  catch (Exception e) {
            	throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR , "시스템 오류");
            }
        });
    }
	
	
	public void ExceptionHandeler(Exception e) {
		if (e instanceof MyBatisSystemException) {
			log.error("MyBatisSystemException {} ", e.getMessage());
			
		} else if (e instanceof BadSqlGrammarException) {
			SQLException se = ((BadSqlGrammarException) e).getSQLException();
			log.error("SQLSyntaxError {} ", se.getMessage());
			
		} else if (e instanceof SQLSyntaxErrorException) {
			log.error("SQLSyntaxErrorException {} ", e.getMessage());
			
		} else if (e instanceof DuplicateKeyException ) {
			log.error("DuplicateKeyException {} ", e.getMessage());
			
		} else if (e instanceof DataIntegrityViolationException) {
			log.error("DataIntegrityViolationException {} ", e.getMessage());
			
		} else if (e instanceof SQLDataException) {
			log.error("SQLDataException {} ", e.getMessage());
			
		} else if (e instanceof IOException) {
			log.error("delete file error : " + e.getMessage());
			
		} else {
			log.error("error : " + e.getMessage());
		}
		
		throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR , "시스템 오류");
	}

}
