package bridge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.CommissionCommentDto;
import bridge.dto.CommissionDetailDto;
import bridge.dto.CommissionDto;
import bridge.dto.ReviewDto;

@Mapper
public interface CommissionMapper {

	List<CommissionDto> getCommissionList(String userId);

	List<CommissionDto> getCommissionDetail(int cIdx);

	int insertCommissionDetail(CommissionDetailDto commissionDetail);

	void editCommissionDetail(CommissionDetailDto commissionDetail);

	void delCommissionDetail(int cdIdx);

	void delCommissionFile(int cdIdx);

	List<CommissionDto> getProgress(int cIdx);

	void commissionEnd(int cIdx);

	void insertCommission(CommissionDto commissionDto);

	void delCommissionList(int cIdx);

	void moneyToUser2(int cIdx);

	int CommissionComment(CommissionCommentDto commissionCommentDto);

	List<CommissionCommentDto> getCommissionComment(int cdIdx);

	int insertReview(ReviewDto reviewDto);

}
