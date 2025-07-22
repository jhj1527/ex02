package com.example.ex02.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.ex02.dto.MemberDto;
import com.example.ex02.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomUserDetailService implements UserDetailsService {
	@Autowired
	private MemberService memberService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("username ▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶▶ " + username);
		
		MemberDto dto = memberService.findById(username);
		
		if (dto == null) {
			throw new UsernameNotFoundException(username);
		}
		
		return new CustomUserDetail(dto);
//		return User.builder()
//				.username(dto.getId())
//				.password(dto.getPassword())
//				.roles(dto.getRole())
//				.build();
	}
}
