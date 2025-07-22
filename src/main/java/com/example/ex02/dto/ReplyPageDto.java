package com.example.ex02.dto;

import java.util.List;

import com.example.ex02.util.PageDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplyPageDto {
	private PageDto pageDto;
	private List<ReplyDto> list;
	private int replyCnt;
}
