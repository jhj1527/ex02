package com.example.ex02.cotroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ex02.service.FileService;
import com.example.ex02.dto.AttachDto;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;

@RestController
@RequestMapping("/api/file/*")
@Slf4j
public class FileController {
	
	private final FileService fileService;
	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}
	
	@PostMapping("/mainUpload")
	public ResponseEntity<?> mainUpload(@RequestParam("file") MultipartFile file, @RequestParam("folderPath") String folderPath) {
		AttachDto dto = fileService.mainUpload(file, folderPath);
		
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
	
	@PostMapping("/multiUpload")
	public ResponseEntity<?> multiUpload(@RequestParam("file") MultipartFile[] file, @RequestParam("folderPath") String folderPath) {
		List<AttachDto> list = fileService.multiUpload(file, folderPath);
		
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	@GetMapping("/getFile")
	public ResponseEntity<?> getFile(@RequestParam("filePath") String filePath, @RequestParam("fileName") String fileName) throws IOException {
		ResponseEntity<byte[]> result = fileService.getFlle(filePath, fileName);
		
		return result;
	}
	
	@GetMapping("/download")
	public ResponseEntity<Resource> download(@RequestHeader("User-Agent") String userAgent, String fileName) {
		ResponseEntity<Resource> result = fileService.download(userAgent, fileName);
		
		return result;
	}
	
	@PostMapping("/delete")
	public ResponseEntity<?> delete(@RequestBody AttachDto dto) {
		fileService.delete(dto);
		
		return new ResponseEntity<>("deleted", HttpStatus.OK);
	}

}
