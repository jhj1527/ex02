package com.example.ex02.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
	private Long ino;
    private String category;
    private String name;
    private Long price;
    private Long amount;
    private Long discount;
    private String content;
    private LocalDate regDate;
    private List<AttachDto> attachList;
}
