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
	
//	@Override
//	public void delCommissionList(int cIdx) {
	
//		CommissionDto commission = commissionMapper.getCommissionByIdx(cIdx);
//		
//		boolean isClient = commission.getUserId1().equals(loginUserId);
//	    boolean isProducer = commission.getUserId2().equals(loginUserId);
//
//	    if (isClient) {
//	        commissionMapper.deleteByClient(cIdx);
//	    } else if (isProducer) {
//	        commissionMapper.deleteByProducer(cIdx);
//	    } else {
//	        throw new IllegalStateException("삭제 권한이 없습니다.");
//	    }
	    // 삭제 표시 후 찐 삭제 조건 확인
	    // 기존의 client/producer 둘 중 한 사람이 작업목록에서 삭제하면 양측 모두 삭제되는 로직에서
	    // commission 테이블에 client/producer 삭제내역 컬럼을 추가하여 list를 불러올 땐 각 삭제하지 않은 목록들을 불러오고
	    // 양측이 모두 삭제한 경우에만 DB에서 삭제되는 아래의 로직을 실행시킴 
//	    CommissionDto updatedCommission = commissionMapper.getCommissionByIdx(cIdx);
//	    if (updatedCommission.isDeletedByClient() && updatedCommission.getDeletedByProducer() == 1) {
//	        commissionMapper.delCommissionList(cIdx); // 실제 삭제 (deleted_yn = 1)
//	    }

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
