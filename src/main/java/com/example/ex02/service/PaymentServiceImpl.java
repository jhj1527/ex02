package com.example.ex02.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ex02.dto.OrderDto;
import com.example.ex02.error.ErrorCode;
import com.example.ex02.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	@Value("${imp.key}")
	private String apiKey;
	@Value("${imp.secret}")
	private String secretKey;
	private final IamportClient iamportClient;
	private final WebClient webClient;

//	@Autowired
	public PaymentServiceImpl(IamportClient iamportClient, WebClient webClient) {
		this.iamportClient = iamportClient;
		this.webClient = webClient;
	}

	@Override
	public String getToken() throws JsonProcessingException {
//		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//		formData.add("imp_key", apiKey);
//		formData.add("imp_secret", secretKey);
		
		Map<String, Object> map = new HashMap<>();
		map.put("imp_key", apiKey);
		map.put("imp_secret", secretKey);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String result = objectMapper.writeValueAsString(map);
		
		String res = webClient.post()
				.uri("/users/getToken")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(result) // 요청 바디 설정 (JSON)
				.retrieve()
				.bodyToMono(String.class)
				.block();
//				.toEntity(String.class).block();
		
		
		JsonNode jsonNode = objectMapper.readTree(res);
		String token = jsonNode.get("response").get("access_token").asText();
		
		return token;
	}

	@Override
	public IamportResponse<Payment> paymentByImpUid(String imp_uid) throws IamportResponseException, IOException {
		IamportClient iamportClient = new IamportClient(apiKey, secretKey);
		return iamportClient.paymentByImpUid(imp_uid);
	}
	
	@Override
	public IamportResponse<Payment> cancelPaymentByImpUid(CancelData data) throws IamportResponseException, IOException {
		return iamportClient.cancelPaymentByImpUid(data);
	}

	@Override
	public Mono<String> paymentByImp_uid(String imp_uid) {
		try {
			String token = getToken();
			
			Mono<String> result =  webClient.get()
					.uri("/payments/{imp_uid}", imp_uid)
					.header("Authorization", token)
					.retrieve()
					.onStatus(httpStatus -> httpStatus.is4xxClientError(),
							clientResponse -> {
								log.error("error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								return Mono.error(new ApiException(ErrorCode.MEMBER_NOTFOUND, "결제 내역 미존재"));
							})
					.bodyToMono(String.class);
//					.toEntity(String.class).block();
			
//			result.subscribe(res -> {
//				log.info(res);
//				return;
//			}, e -> {
//				log.error("error ::::::::::::::::::" + e.getMessage());
//				Mono.error(new ApiException(ErrorCode.MEMBER_NOTFOUND, "결제 내역 미존재"));
//			});
			
			return Mono.just(result.block());
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	@Transactional
	public Mono<String> cancel(OrderDto dto) {
		try {
//			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//			int amount = dto.getOrderPrice() + dto.getCharge();
//			map.add("imp_uid", dto.getImp_uid());
//			map.add("amount", amount);
			
			Map<String, Object> map = new HashMap<>();
			map.put("imp_uid", dto.getImp_uid());
			
			if (dto.getOrderPrice() > 0) {
				int amount = dto.getOrderPrice() + dto.getCharge();
				map.put("amount", amount);
			}
			
			ObjectMapper objectMapper = new ObjectMapper();
			String formData = objectMapper.writeValueAsString(map);
			
			String token = getToken();
			
			Mono<String> result =  webClient.get()
					.uri("/payments/{imp_uid}", dto.getImp_uid())
					.header("Authorization", token)
					.retrieve()
					.onStatus(httpStatus -> httpStatus.is4xxClientError(),
							clientResponse -> {
								return Mono.error(new ApiException(ErrorCode.MEMBER_NOTFOUND, "결제 내역 미존재"));
							})
					.bodyToMono(String.class)
					.flatMap(data -> {
//						log.info(data);
						return webClient.post()
								.uri("/payments/cancel")
								.contentType(MediaType.APPLICATION_JSON)
								.header("Authorization", token)
								.bodyValue(formData)
								.retrieve()
								.bodyToMono(String.class);
//								.toEntity(String.class).block();
					});
			
			result.subscribe(res -> {
				log.info(res);
				
			}, e -> {
				log.error("error ::::::::::::::::::" + e.getMessage());
				Mono.error(new ApiException(ErrorCode.INTERNAL_SERVER_ERROR));
			});
			
			return Mono.just(result.block());
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
