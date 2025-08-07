package com.example.ex02.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ex02.dto.AttachDto;
import com.example.ex02.dto.CartDto;
import com.example.ex02.dto.ItemDto;
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
		List<CartDto> list = cartMapper.getList(id);
		
		list.stream().forEach(cart -> {
			List<AttachDto> AttachList = cart.getAttachList()
					.stream()
					.filter(attach -> attach.getFileName().startsWith("main"))
					.collect(Collectors.toList());
			
			cart.setAttachList(AttachList);
		});
		
		return list;
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
		
		if (cnt - dto.getQuantity() < 0) {
			throw new ApiException(ErrorCode.EXCEED_CART, "수량초과");
		}
		
		itemMapper.amountUpdate(dto.getIno(), -dto.getQuantity());
		
		CartDto cartDto = get(dto);
	
		if (cartDto != null) {
//			if (cartDto.getQuantity() + dto.getQuantity() > 10) {
//				throw new ApiException(ErrorCode.EXCEED_CART, "수량초과");
//			}
			throw new ApiException(ErrorCode.DUPLICATION, "카트중복");
		}
		
		cartMapper.insert(dto);
	}
	
	@Transactional
	@Override
	public void update(CartDto dto) throws Exception {
		CartDto cartDto = get(dto);
		
		if (cartDto != null) {
//			if (cartDto.getQuantity() + dto.getQuantity() > 10) {
//				throw new ApiException(ErrorCode.EXCEED_CART, "수량초과");
//			}
			
			itemMapper.amountUpdate(dto.getIno(), -dto.getQuantity());
			
			dto.setCno(cartDto.getCno());
			cartMapper.update(dto);
		}
	}

	@Transactional
	@Override
	public void updateList(List<CartDto> list) throws Exception {
//		log.info(list.toString());
		
		list.stream().filter(dto -> {
			ItemDto itemDto = itemMapper.get(dto.getIno());
			CartDto cartDto = get(dto);
			
			// 상품 재고 - 변경된 카트수량 - 기존 카트 수량이 0보다 큰경우  
			return itemDto.getAmount() - (dto.getQuantity() - cartDto.getQuantity()) >= 0;
		}).forEach(dto -> {
			CartDto cartDto = get(dto);
			
			// 변경된 카트수량 - 기존 카트 수량만큼 상품재고 변경  
			itemMapper.amountUpdate(dto.getIno(), cartDto.getQuantity() - dto.getQuantity());
		});
		
		cartMapper.updateList(list);
	}

	@Transactional
	@Override
	public void delete(Long cno) throws Exception {
		CartDto dto = cartMapper.getByCno(cno); 
		
		itemMapper.amountUpdate(dto.getIno(), dto.getQuantity());
		
		cartMapper.delete(cno);
	}

}
