package com.example.ex02.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PieChartDto {
	private LocalDate month;
	private String category;
	private int count;
	private double percent;
}
