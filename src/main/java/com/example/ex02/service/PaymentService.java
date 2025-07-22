package com.example.ex02.service;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

import com.example.ex02.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import reactor.core.publisher.Mono;

public interface PaymentService {
	String getToken() throws JsonProcessingException;
	
	IamportResponse<Payment> paymentByImpUid(String imp_uid) throws IamportResponseException, IOException;
	
	IamportResponse<Payment> cancelPaymentByImpUid(CancelData data) throws IamportResponseException, IOException;
	
	Mono<String> paymentByImp_uid(String imp_uid);

	Mono<String> cancel(OrderDto dto);
	
}