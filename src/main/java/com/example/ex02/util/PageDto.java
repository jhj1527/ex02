package com.example.ex02.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class PageDto {
	private CriteriaDto criteriaDto;
	private int total;
	private int startPage;
	private int endPage;
	private boolean next;
	private boolean prev;
	
	public PageDto(CriteriaDto criteriaDto, int total) {
		this.criteriaDto = criteriaDto;
		this.total = total;
		this.endPage = (int)Math.ceil(criteriaDto.getPageNum() / 10.0) * 10;
		this.startPage = this.endPage - 9;
		int realEndPage = (int)Math.ceil((total * 1.0) / criteriaDto.getAmount());
		if (realEndPage < this.endPage) this.endPage = realEndPage;
		this.prev = this.startPage > 1;
		this.next = this.endPage < realEndPage;
	}

}
