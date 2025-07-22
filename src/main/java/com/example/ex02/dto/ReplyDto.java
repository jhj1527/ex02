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
public class ReplyDto {
	private Long rno;
	private Long bno;
	private String reply;
	private String id;
	private LocalDateTime regDate;
	private LocalDate updateDate;
}
