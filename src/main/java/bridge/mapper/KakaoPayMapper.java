package bridge.mapper;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.ApproveResponseDto;
@Mapper
public interface KakaoPayMapper {

	 public void insertPay(ApproveResponseDto approveResponse);

	public void kakaoToList();

}
