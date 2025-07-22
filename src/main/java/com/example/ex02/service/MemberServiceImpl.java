package com.example.ex02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ex02.dto.MemberDto;
import com.example.ex02.error.ErrorCode;
import com.example.ex02.exception.ApiException;
import com.example.ex02.mapper.MemberMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService{

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
    	this.memberMapper = memberMapper;
    	this.passwordEncoder = passwordEncoder;
    }
    
	@Override
	public MemberDto findById(String id) {
		return memberMapper.findById(id);
	}


	@Override
	public MemberDto findByPassword(String password) {
		return memberMapper.findByPassword(password);
	}

	@Override
	@Transactional
	public int insert(MemberDto dto) {
		MemberDto memberDto = findById(dto.getId());
		
		if (memberDto != null) {
			throw new ApiException(ErrorCode.DUPLICATION, "아이디 중복");
		}
		
		if (dto.getId().startsWith("admin")) {
			dto.setRole("ADMIN");
		}
		
		dto.setPassword(passwordEncoder.encode(dto.getPassword()));
		
		return memberMapper.insert(dto);
	}

}
