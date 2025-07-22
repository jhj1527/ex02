package com.example.ex02.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.ex02.error.ErrorCode;
import com.example.ex02.exception.ApiException;
import com.example.ex02.dto.AttachDto;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
	private final String uploadFolder = "D:\\upload";
	
	@Override
	public AttachDto mainUpload(MultipartFile file, String folderPath) {
		File uploadPath = new File(uploadFolder, folderPath);
		
		log.info("uploadPath ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ " + uploadPath);
		
		// 폴더 없으면 생성
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}
		
		log.info("====================================");
		log.info("upload File Name : " + file.getOriginalFilename());
		log.info("upload File Size : " + file.getSize());
		
		String uploadFileName = file.getOriginalFilename();
		
		// IE has file path
		uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
		
		log.info("uploadFileName ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ " + uploadFileName);
		
		UUID uuid = UUID.randomUUID();
		uploadFileName = "main" + "_" + uploadFileName;
		
		File saveFile = new File(uploadPath, uploadFileName);
		
		try {
			AttachDto dto = AttachDto.builder()
					.attachId(uuid.toString())
					.filePath(folderPath)
					.fileName(uploadFileName)
					.fileType(checkImageType(saveFile))
					.build();
			
			file.transferTo(saveFile);
			
			return dto;
			
			// check Image type file
//			if (dto.isFileType()) {
//				
//				FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));
//				Thumbnailator.createThumbnail(file.getInputStream() , thumbnail , 200 , 200);
//				
//				thumbnail.close();
//				
//			} else {
//				file.transferTo(saveFile);
//			}
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public List<AttachDto> multiUpload(MultipartFile[] file, String folderPath) {
		List<AttachDto> list = new ArrayList<>();
		File uploadPath = new File(uploadFolder, folderPath);
		
//		log.info("uploadPath ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ " + uploadPath);
		
		// 폴더 없으면 생성
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}
		
		for (MultipartFile multipartFile : file) {
//			log.info("====================================");
//			log.info("upload File Name : " + multipartFile.getOriginalFilename());
//			log.info("upload File Size : " + multipartFile.getSize());
			
			String uploadFileName = multipartFile.getOriginalFilename();
			
			// IE has file path
			uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
			
//			log.info("uploadFileName ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ " + uploadFileName);
			
			UUID uuid = UUID.randomUUID();
			uploadFileName = "sub" + "_" + uploadFileName;
			
			try {
				File saveFile = new File(uploadPath, uploadFileName);
				
				AttachDto dto = AttachDto.builder()
						.attachId(uuid.toString())
						.filePath(folderPath)
						.fileName(uploadFileName)
						.fileType(checkImageType(saveFile))
						.build();
				
				list.add(dto);
				
//				multipartFile.transferTo(saveFile);
				
				FileOutputStream thumbnail = new FileOutputStream(saveFile);
				Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 200, 200);
//
				thumbnail.close();
				
			} catch (Exception e) {
				e.printStackTrace();
				
				throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR , "시스템 오류");
			}
		}
		
		return list;
	}

	@Override
	public ResponseEntity<Resource> download(String userAgent, String fileName) {
		Resource resource = new FileSystemResource(uploadFolder + "\\" + fileName);
		
		log.info("resource ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ : " + resource);

		if (!resource.exists()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		String resourceName = resource.getFilename();
		// remove uuid
		String resourceOriginalName = resourceName.substring(resourceName.indexOf("_") + 1);
		HttpHeaders headers = new HttpHeaders();

		try {
			String downloadName = null;

			if (userAgent.contains("Trident")) {
				log.info("IE browser");
				downloadName = URLEncoder.encode(resourceOriginalName, "UTF-8").replaceAll("\\+", " ");

			} else if (userAgent.contains("Edge")) {
				log.info("Edge browser");
				downloadName = URLEncoder.encode(resourceOriginalName, "UTF-8");

			} else {
				log.info("Chrome browser");
				downloadName = new String(resourceOriginalName.getBytes("UTF-8"), "ISO-8859-1");
			}

			log.info("downloadName : " + downloadName);

			headers.add("content-Disposition", "attachment; filename=" + downloadName);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR , "시스템 오류");
		}

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> getFlle(String filePath, String fileName) {
		try {
			File file = new File(uploadFolder + "\\" + filePath + "\\" + fileName);
			HttpHeaders header = new HttpHeaders();
			header.add("Content-Type", Files.probeContentType(file.toPath()));
			
			return ResponseEntity.ok()
					.headers(header)
					.body(FileCopyUtils.copyToByteArray(file));
			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR , "시스템 오류");
		}
	}

	@Override
	public void delete(AttachDto dto) {
		try {
			File file = new File(uploadFolder + "\\" + dto.getFilePath().trim() + "\\" 
					+ URLDecoder.decode(dto.getFileName(), "UTF-8"));
			
//			log.info("file : " + file.toString());
			
			if (file.exists()) file.delete();
			
            File[] directoryList = file.getParentFile().listFiles();

            if (directoryList.length == 0) {
            	file.getParentFile().delete();
            }

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR , "시스템 오류");
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR , "시스템 오류");
		}
	}
	
	private boolean checkImageType(File file) {
		try {
			String contentType = Files.probeContentType(file.toPath());
			
			return contentType.startsWith("image");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

        return false;
	}
}
