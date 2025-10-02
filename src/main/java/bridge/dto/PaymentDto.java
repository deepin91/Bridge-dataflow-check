package bridge.dto;

import lombok.Data;

@Data
public class PaymentDto {

    private int paymentIdx;
    private String clients;		//의뢰인 주는사람	-> user1
    private String producer;	//제작자 받는사람	-> user2
//    private int deposit;		//예치금 -> 삭제
    private int downpayment;	//거래 금액
    private int totalCost;		//총액
    private int usepoint;		//포인트 사용액
//    private int clientPoint;	//보유 포인트
    //시간이 없음
    private String paymentDt;
}
