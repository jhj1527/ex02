package com.example.ex02.cotroller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.ex02.dto.OrderDto;
import com.example.ex02.dto.OrderDto.OrderItemDto;
import com.example.ex02.exception.ApiException;
import com.example.ex02.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;import jdk.jshell.spi.ExecutionControl.RunException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/payment")
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@Autowired
	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@GetMapping("/getToken")
	public ResponseEntity<?> getToken() throws IamportResponseException, IOException, InterruptedException, ParseException {
		return new ResponseEntity<>(paymentService.getToken(), HttpStatus.OK);
	}

//	@GetMapping("/{imp_uid}")
//	public IamportResponse<?> paymentByImpUid(@PathVariable("imp_uid") String imp_uid)
//			throws IamportResponseException, IOException {
//		return paymentService.paymentByImpUid(imp_uid);
//	}
//	
//	@PostMapping("/cansel")
//	public IamportResponse<Payment> cancelPaymentByImpUid(@RequestBody CancelData data)
//			throws IamportResponseException, IOException {
//		return paymentService.cancelPaymentByImpUid(data);
//	}
	
	@GetMapping("/{imp_uid}")
	public Mono<String> paymentByImp_uid(@PathVariable("imp_uid") String imp_uid) throws JsonProcessingException {
		try {
			Mono<String> getPayment = paymentService.paymentByImp_uid(imp_uid);
		
			return getPayment;
			
		} catch (ApiException e) {
			log.error("getMessage!!!!!!!!!!!!! : " + e.getMessage());
			Map <String, Object> map = new HashMap<>();
			map.put("message", e.getMessage());
			
			return Mono.just(new ObjectMapper().writeValueAsString(map));
		}
	}
	
	@PostMapping("/cancel")
	public Mono<String> cancel(@RequestBody OrderItemDto dto) throws JsonProcessingException  {
		try {
			Mono<String> delete = paymentService.cancel(dto);
			
			return delete;
			
		} catch (ApiException e) {
			log.error("getMessage!!!!!!!!!!!!! : " + e.getMessage());
			Map <String, Object> map = new HashMap<>();
			map.put("message", e.getMessage());
			map.put("status", e.getError().getType());
			map.put("code", e.getError().getCode());
			
			return Mono.just(new ObjectMapper().writeValueAsString(map));
		}
//		return null;
	}

}
