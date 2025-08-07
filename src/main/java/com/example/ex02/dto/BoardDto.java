package com.example.ex02.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDto {
	private Long bno;
	private String title;
	private String content;
	private LocalDateTime regDate;
	private LocalDateTime updateDate;
	private String id;
	private int viewCount;
	private int replyCount;
}
