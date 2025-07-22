package com.example.ex02.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.ex02.dto.MemberDto;

public class CustomUserDetail implements UserDetails {
	private static final long serialVersionUID = 1L;
	private MemberDto memberDto;
	
	public CustomUserDetail(MemberDto memberDto) {
		this.memberDto = memberDto;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new SimpleGrantedAuthority("ROLE_" + memberDto.getRole()));
		return collect;
	}

	@Override
	public String getPassword() {
		return memberDto.getPassword();
	}

	@Override
	public String getUsername() {
		return memberDto.getId();
	}

}
