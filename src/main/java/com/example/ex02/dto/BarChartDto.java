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
public class BarChartDto {
	private LocalDate month;
	private int count; 
	private Long sumPrice; 
	private double avgPrice; 
}
