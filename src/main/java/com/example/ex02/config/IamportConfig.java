package com.example.ex02.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.siot.IamportRestClient.IamportClient;

@Configuration
public class IamportConfig {
	@Value("${imp.key}")
	private String apiKey;
	@Value("${imp.secret}")
	private String secretKey;
	
	@Bean
	public IamportClient iamportClient() {
		return new IamportClient(apiKey, secretKey);
	}
	
	@Bean
	public WebClient webClient() {
		return WebClient
				.builder()
				.baseUrl("https://api.iamport.kr")
//				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build(); 
	}
}
