package com.example.ex02.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ex02.dto.CartDto;
import com.example.ex02.error.ErrorCode;
import com.example.ex02.exception.ApiException;
import com.example.ex02.mapper.CartMapper;
import com.example.ex02.mapper.ItemMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
	private final CartMapper cartMapper;
	private final ItemMapper itemMapper;
	
	@Autowired
	public CartServiceImpl(CartMapper cartMapper, ItemMapper itemMapper) {
		this.cartMapper = cartMapper;
		this.itemMapper = itemMapper;
	}
	
	@Override
	public List<CartDto> getList(String id) {
		return cartMapper.getList(id);
	}
	
	@Override
	public List<CartDto> getCheckList(int[] checkArr) {
		return cartMapper.getCheckList(checkArr);
	}
	
	@Override
	public CartDto get(CartDto dto) {
		return cartMapper.get(dto);
	}
	
	@Override
	public int[] totalPrice(String id) {
		return cartMapper.totalPrice(id);
	}
	
	@Override
	public int getCount(String id) {
		return cartMapper.getCount(id);
	}
	
	@Transactional
	@Override
	public void insert(CartDto dto) throws Exception {
		int cnt = itemMapper.getAmountCount(dto.getIno());
		
		if (cnt - dto.getAmount() < 0) {
			throw new ApiException(ErrorCode.EXCEED_CART);
		}
		
		itemMapper.amountUpdate(dto.getIno(), -dto.getAmount());
		
		CartDto cartDto = get(dto);
		
		if (cartDto != null) {
			throw new ApiException(ErrorCode.DUPLICATION, "카트 중복");
		}
		
		cartMapper.insert(dto);
	}
	
	@Transactional
	@Override
	public void update(CartDto dto) throws Exception {
		CartDto cartDto = get(dto);
		
		if (cartDto != null) {
			if (cartDto.getAmount() + dto.getAmount() > 10) {
				throw new ApiException(ErrorCode.EXCEED_CART);
			}
			
			dto.setCno(cartDto.getCno());
			cartMapper.update(dto);
		}
	}

	@Transactional
	@Override
	public void updateList(List<CartDto> list) throws Exception {
		try {
			list = list.stream().filter(dto -> {
				int cnt = itemMapper.getAmountCount(dto.getIno());
				return cnt - dto.getAmount() > 0;
			}).collect(Collectors.toList());
			
			list.forEach(dto -> {
				itemMapper.amountUpdate(dto.getIno(), -dto.getAmount());
			});
			
			cartMapper.updateList(list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	@Override
	public void delete(Long cno) throws Exception {
		try {
			cartMapper.delete(cno);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
