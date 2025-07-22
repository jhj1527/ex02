package com.example.ex02.util;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CriteriaDto {
	private int pageNum;
	private int amount;
	private String type;
	private String keyword;
	
	public CriteriaDto() {
		this.pageNum = 1;
		this.amount = 10;
	}
	
	public CriteriaDto(int pageNum, int amount) {
		this.pageNum = pageNum;
		this.amount = amount;
	}
	
	public CriteriaDto(int pageNum, int amount, String type, String keyword) {
		this.pageNum = pageNum;
		this.amount = amount;
		this.type = type;
		this.keyword = keyword;
	}
}
