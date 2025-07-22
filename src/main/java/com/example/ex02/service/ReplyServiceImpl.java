package com.example.ex02.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ex02.dto.ReplyDto;
import com.example.ex02.error.ErrorCode;
import com.example.ex02.exception.ApiException;
import com.example.ex02.mapper.BoardMapper;
import com.example.ex02.mapper.ReplyMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReplyServiceImpl implements ReplyService {
	@Autowired
	public ReplyServiceImpl(ReplyMapper replyMapper, BoardMapper boardMapper) {
		this.replyMapper = replyMapper;
		this.boardMapper = boardMapper;
	}

	private final ReplyMapper replyMapper;
	private final BoardMapper boardMapper;

	@Override
	public List<ReplyDto> getList(Map<String, Object> map) {
		return replyMapper.getList(map);
	}

	@Override
	public int getTotalCount(Long bno) {
		return replyMapper.getTotalCount(bno);
	}

	@Override
	public ReplyDto get(Long rno) {
		return replyMapper.get(rno);
	}

	@Override
	@Transactional
	public int insert(ReplyDto dto) {
		if (replyMapper.insert(dto) != 1) {
			throw new ApiException(ErrorCode.BAD_REQUEST);
		}

		if (boardMapper.replyCountupdate(dto.getBno(), 1) != 1) {
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "시스템 오류");
		}

		return 1;
	}

	@Override
	@Transactional
	public boolean update(ReplyDto dto) {
		boolean result = replyMapper.update(dto) == 1;

		if (!result) {
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "시스템 오류");
		}

		return result;
	}

	@Override
	@Transactional
	public boolean delete(Long rno) {
		ReplyDto dto = replyMapper.get(rno);

		if (dto == null) {
			throw new ApiException(ErrorCode.MEMBER_NOTFOUND);
		}

		if (boardMapper.replyCountupdate(dto.getBno(), -1) != 1) {
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "시스템 오류");
		}

		boolean result = replyMapper.delete(rno) == 1;

		if (!result) {
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "시스템 오류");
		}

		return result;
	}
}
