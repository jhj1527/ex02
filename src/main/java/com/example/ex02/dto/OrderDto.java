package com.example.ex02.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
	private String orderId;
	private String id;
	private String imp_uid; // import 결제 조회 및 취소시 필요
	private String postCode;
	private String address1;
	private String address2;
	private String address3;
	private String phone;
	private String email;
	private int orderPrice;
	private int charge;
	private int state;
	private LocalDateTime regDate;
	private List<OrderItemDto> list;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderItemDto{
		private Long oino;
		private String orderId;
		private Long ino;
		private String id;
		private String name;
		private int quantity;
		private int price;
		private int discount;
		private AttachDto attachDto;
	}
}
