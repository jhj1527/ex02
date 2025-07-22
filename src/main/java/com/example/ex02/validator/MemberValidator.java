package com.example.ex02.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.ex02.dto.MemberDto;

@Component
public class MemberValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return MemberDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MemberDto dto = (MemberDto) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "required", "아이디 필수");
		
		if (!StringUtils.hasText(dto.getPassword())) {
			errors.rejectValue("password", "required", "비밀번호 필수");
		}
	}
}
