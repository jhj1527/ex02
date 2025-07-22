package com.example.ex02.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
	private Long cno;
	private String id;
	private Long ino;
	private String name;
	private int price;
	private int amount;
	private List<AttachDto> attachList;
}
