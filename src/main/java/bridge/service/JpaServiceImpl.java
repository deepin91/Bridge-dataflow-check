package bridge.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bridge.dto.ChattingRoomLastMessageDto;
import bridge.dto.NoticeDto;
import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;
import bridge.mapper.NoticeMapper;
import bridge.repository.JpaChattingRepository;
import bridge.repository.JpaMessageRepository;

@Service
public class JpaServiceImpl implements JpaService {
	@Autowired
	private JpaMessageRepository jpaMessageRepository;
	@Autowired
	private JpaChattingRepository jpaChattingRepository;
	@Autowired
	private NoticeMapper noticeMapper;
	
	@Override
	public List<MessageEntity> getMessage(int roomIdx) {
		return (List<MessageEntity>) jpaMessageRepository.findByRoomIdxOrderByCreatedTimeDesc(roomIdx);
	}
	
	@Override
	public void insertData(MessageEntity messageEtity) {
		jpaMessageRepository.save(messageEtity);
	}

	@Override
	public ChattingEntity getchatting(int roomIdx) {
		Optional<ChattingEntity> optional = jpaChattingRepository.findById(roomIdx);
		ChattingEntity chatting = optional.get();
		return chatting;
	}

	@Override
	public List<ChattingEntity> getChattingRoom(String userId) {
		List<ChattingEntity> a = (List<ChattingEntity>) jpaChattingRepository.findByUserId1(userId);
		List<ChattingEntity> b = (List<ChattingEntity>) jpaChattingRepository.findByUserId2(userId);
		System.out.println("aaaaaaaaaaaaaaaaaaaaaa" + a+"BBBBBBBBBBBBBBBBBBBBBBBBBBB" + b);
		if(a != null) {
			return a;
		}else {
			return b;
		}
	}
	@Override
	public List<NoticeDto> noticeList() throws Exception {
		return noticeMapper.noticeList();
	}

	@Override
	public void insertNotice(NoticeDto noticeDto) throws Exception {
		noticeMapper.insertNotice(noticeDto);
		
	}

	@Override
	public NoticeDto noticeDetail(int noticeIdx) throws Exception {
		return noticeMapper.noticeDetail(noticeIdx);
	}

	@Override
	public NoticeDto selectNoticeDetail(int noticeIdx) throws Exception {
		return noticeMapper.selectNoticeDetail(noticeIdx);
	}

	@Override
	public int updateNotice(NoticeDto noticeDto) throws Exception {
		return noticeMapper.updateNotice(noticeDto);
	}

	@Override
	public int deleteNotice(int noticeIdx) throws Exception {
		return noticeMapper.deleteNotice(noticeIdx);
	}

	@Override
	public int selectNoticeListCount() throws Exception {
		return noticeMapper.selectNoticeListCount();
	}
	
	/* 개선사항 -- 기존 채팅방 존재 여부 체크 >  insert할지 말지 설정해야함
	 * 현재 중복체크가 없어서 동일 유저 둘이서 다시 채팅할 경우 기존 채팅방 불러오기가 아닌 중복된 채팅방이 늘어남  
	 */
//	@Override
//    public void openChat(ChattingEntity chattingEntity) {
//		if(jpaChattingRepository.findByUserId1AndUserId2(chattingEntity.getUserId1(),chattingEntity.getUserId2()).size() < 1
//		        && jpaChattingRepository.findByUserId1AndUserId2(chattingEntity.getUserId2(),chattingEntity.getUserId1()).size() < 1
//				) {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//	        jpaChattingRepository.save(chattingEntity);  // 여기서 한번 save되고
//
//		}
//		System.out.println(">>>>>>>>>>>오픈챗 서비스");
//        jpaChattingRepository.save(chattingEntity); // 여기서 조건과 상관 없이 save 됨 -> 조건 만족하면 총 2번의 save가 되는 꼴 = 중복방지X 
//        System.out.println(">>>>>>>>>>>오픈챗 서비스 나옴");
//    }
	
	@Override
	public int openOrFindChat(ChattingEntity chattingEntity) {
		String userA =  chattingEntity.getUserId1();
		String userB = chattingEntity.getUserId2();
		
		// userA와 B 바꿔가며 중복조회
		List<ChattingEntity> direct = jpaChattingRepository.findByUserId1AndUserId2(userA, userB);
		List<ChattingEntity> reverse = jpaChattingRepository.findByUserId1AndUserId2(userB, userA);
		
		if(!direct.isEmpty()) {
			return direct.get(0).getRoomIdx();
		}
		if(!reverse.isEmpty()) {
			return reverse.get(0).getRoomIdx();
		}
		ChattingEntity newChatRoom = jpaChattingRepository.save(chattingEntity);
		return newChatRoom.getRoomIdx();
	}
	
	@Override
	public List<ChattingRoomLastMessageDto> getChattingRoomMessage(String userId){
		
		// - 로그인한 유저가 속한 채팅방 전체 조회 (userId1 또는 userId2가 본인인 경우)
		// (로그인한 유저가 속한 채팅방을 userId1 또는 userId2 기준으로 모두 조회한 후 중복 제거)
		List<ChattingEntity> chatRooms = Stream.concat(
				jpaChattingRepository.findByUserId1(userId).stream(), // userId1로 참여한 방
				jpaChattingRepository.findByUserId2(userId).stream() // userId2로 참여한 방
		).distinct() // 동일한 채팅방이 2번 나올 수 있으니 중복제거 
		 .collect(Collectors.toList());
		
		// - 각 채팅방에 대해 마지막 메시지 뽑고, DTO로 매핑
		List<ChattingRoomLastMessageDto> result = new ArrayList<>();
		for(ChattingEntity chatRoom : chatRooms) {
			int roomIdx = chatRoom.getRoomIdx(); // 현재 채팅방 index 추출
			
			// 해당 채팅방(roomIdx)의 모든 메시지를 조회
			// (해당 채팅방의 메시지 중 가장 마지막 메시지 (createdTime 기준으로 가장 마지막))
			List<MessageEntity> messages = jpaMessageRepository.findByRoomIdx(roomIdx);
			
			// 메시지가 없는 경우를 대비한 기본값 초기화
			String lastMessage = "";
			LocalDateTime lastSentTime = null;
			
			// 메시지가 하나 이상 있을 때 가장 마지막 메시지 추출
			if(!messages.isEmpty()) {
				MessageEntity last = messages.get(messages.size() -1); // 마지막 메시지 -  리스트 인덱스는 0부터 시작하므로 
				lastMessage = last.getData(); // 메시지 내용
				lastSentTime = last.getCreatedTime(); // 메시지 생성 시간 (필요 시 MessageEntity에 추가)
			}
			// 추출한 정보로 DTO 생성 및 채우기
			ChattingRoomLastMessageDto chattingRoomLastMessageDto = new ChattingRoomLastMessageDto();
			chattingRoomLastMessageDto.setRoomIdx(roomIdx);
			chattingRoomLastMessageDto.setUserId1(chatRoom.getUserId1());
			chattingRoomLastMessageDto.setUserId2(chatRoom.getUserId2());
			chattingRoomLastMessageDto.setLastMessage(lastMessage);
			chattingRoomLastMessageDto.setLastSentTime(lastSentTime);
			
			// 결과 리스트에 추가
			result.add(chattingRoomLastMessageDto);
		}
			// 채팅방 목록을 마지막 메세지 시간 기준으로 정렬(최신순)
			result.sort(Comparator.comparing(ChattingRoomLastMessageDto::getLastSentTime,
					Comparator.nullsLast(Comparator.reverseOrder())));
		
		return result; // <-- 모든 채팅방에 대해 DTO 리스트 반환
	} // <-- GET /api/chatroom/list 요청 시 로그인한 사용자의 모든 채팅방 리스트가 불려옴 > 각 채팅방의 roomIdx, 대화상대, 마지막 메세지, 시간 까지 응답
	  // 읽음처리는 추후 설정 고려중
	  // *****프론트에서 실시간으로 채팅목록 (최신순으로) 정렬하려면 따로 설정해야함****
	
//	@Override
//	public void openChat(ChattingEntity chattingEntity) {
//		String userA =  chattingEntity.getUserId1();
//		String userB = chattingEntity.getUserId2();
//		
//		boolean isDuplicateRoom = 
//				!jpaChattingRepository.findByUserId1AndUserId2(userA, userB).isEmpty() ||
//				!jpaChattingRepository.findByUserId1AndUserId2(userB, userA).isEmpty();
//		if(!isDuplicateRoom) {
//			System.out.println("새로운 채팅방 생성");
//			jpaChattingRepository.save(chattingEntity);
//		} else {
//			System.out.println("이미 존재하는 채팅방입니다.");
//		}
//	}

}
