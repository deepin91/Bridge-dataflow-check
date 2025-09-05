package bridge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bridge.dto.CommissionCommentDto;
import bridge.dto.CommissionDetailDto;
import bridge.dto.CommissionDto;
import bridge.dto.ReviewDto;
import bridge.mapper.CommissionMapper;

@Service
public class CommissionServiceImpl implements CommissionService {
	
	@Autowired
	CommissionMapper commissionMapper;

	@Override
	public List<CommissionDto> getCommissionList(String userId) {
		return commissionMapper.getCommissionList(userId);
	}

	@Override
	public List<CommissionDto> getCommissionDetail(int cIdx) {
		return commissionMapper.getCommissionDetail(cIdx);
	}

	@Override
	public int insertCommissionDetail(CommissionDetailDto commissionDetail) {
		return commissionMapper.insertCommissionDetail(commissionDetail);
	}

	@Override
	public void editCommissionDetail(CommissionDetailDto commissionDetail) {
		commissionMapper.editCommissionDetail(commissionDetail);
	}

	@Override
	public void delCommissionDetail(int cdIdx) {
		commissionMapper.delCommissionDetail(cdIdx);
	}

	@Override
	public void delCommissionFile(int cdIdx) {
		commissionMapper.delCommissionFile(cdIdx);
	}

	@Override
	public List<CommissionDto> getProgress(int cIdx) {
		return commissionMapper.getProgress(cIdx);
	}

	@Override
	public void commissionEnd(int cIdx) {
		commissionMapper.commissionEnd(cIdx);
	}

	@Override
	public void insertCommission(CommissionDto commissionDto) {
		commissionMapper.insertCommission(commissionDto);
	}

	@Override
	public void delCommissionList(int cIdx) {
		commissionMapper.delCommissionList(cIdx);
	}

	@Override
	public void moneyToUser2(int cIdx) {
		commissionMapper.moneyToUser2(cIdx);
	}

	@Override
	public int CommissionComment(CommissionCommentDto commissionCommentDto) {
		return commissionMapper.CommissionComment(commissionCommentDto);
	}

	@Override
	public List<CommissionCommentDto> CommissionComment(int cdIdx) {
		return commissionMapper.getCommissionComment(cdIdx);
	}

	@Override
	public int insertReview(ReviewDto reviewDto) {
		return commissionMapper.insertReview(reviewDto);
	}

}
