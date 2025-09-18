package bridge.service;

import java.util.List;

import bridge.dto.ChattingRoomLastMessageDto;
import bridge.dto.NoticeDto;
import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;

public interface JpaService {
	List<MessageEntity> getMessage(int roomIdx);
	void insertData(MessageEntity messageEtity);
	ChattingEntity getchatting(int roomIdx);
	List<ChattingEntity> getChattingRoom(String userId);
	List<NoticeDto> noticeList()throws Exception;
	void insertNotice(NoticeDto noticeDto)throws Exception;
	NoticeDto noticeDetail(int noticeIdx)throws Exception;
	NoticeDto selectNoticeDetail(int noticeIdx)throws Exception;
	int deleteNotice(int noticeIdx)throws Exception;
	int updateNotice(NoticeDto noticeDto)throws Exception;
	int selectNoticeListCount() throws Exception;
//	void openChat(ChattingEntity chattingEntity);
	int openOrFindChat(ChattingEntity chattingEntity); 
	List<ChattingRoomLastMessageDto> getChattingRoomMessage(String usedId);
	// 기존의 openChat은 중복체크도 안돌아가고 단지 채팅방 생성 기능만 했으므로 
	// openOrFindChat() 메서드 추가해서 동일 유저끼리의 채팅방이 존재하는지 확인 후 있으면 기존 채팅 대화 이어나가기
	// 없는경우 새로운 채팅방 생성하도록 처리

}
