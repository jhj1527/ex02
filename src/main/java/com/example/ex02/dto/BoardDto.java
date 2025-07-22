package com.example.ex02.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDto {
	@Id
	private Long bno;
	@Column
	private String title;
	@Column
	private String content;
	@Column
	private LocalDateTime regDate;
	@Column
	private LocalDateTime updateDate;
	@Column
	private String id;
	@Column
	private int viewCount;
	@Column
	private int replyCount;
}
