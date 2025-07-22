package com.example.ex02.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachDto {
	private String attachId;
	private Long ino;
	private String filePath;
	private String fileName;
	private boolean fileType;
}
