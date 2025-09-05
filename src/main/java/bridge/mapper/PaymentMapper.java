package bridge.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.PayListDto;
import bridge.dto.PaymentDto;
import bridge.dto.UserDto;

@Mapper
public interface PaymentMapper {

    List<PaymentDto> paymentList()throws Exception;
    void insertPayment(PaymentDto paymentDto)throws Exception;
    int paymentDetail(String userId)throws Exception;
	void doPayment(PaymentDto paymentDto);
	void updatePoint(HashMap<String, Object> map);
	void updatePartnerMoney();
	int insertPayment();
	//관리자용 결제 내역
	List<PayListDto> payListAll();
	//관리자용 거래 내역
	List<PayListDto> payListDeal();
	//관리자용 충전 내역
	List<PayListDto> payListCharge();
	//회원용 결제 내역
	List<PayListDto> payList(String userId);
	List<PayListDto> payDeal(String userId);
	List<PayListDto> payCharge(String userId);
	
	

}
