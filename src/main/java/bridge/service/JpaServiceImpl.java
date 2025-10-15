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
import org.springframework.transaction.annotation.Transactional;

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
		List<MessageEntity> messages = jpaMessageRepository.findByRoomIdxOrderByCreatedTimeAsc(roomIdx);

		System.out.println("🔎 roomIdx: " + roomIdx);
		System.out.println("🔎 메시지 수: " + messages.size());
		for (MessageEntity msg : messages) {
			System.out.println(" - " + msg.getWriter() + ": " + msg.getData());
		}
		return messages;
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
		List<ChattingEntity> a = jpaChattingRepository.findByUserId1(userId);
		List<ChattingEntity> b = jpaChattingRepository.findByUserId2(userId);
//		System.out.println("aaaaaaaaaaaaaaaaaaaaaa" + a+"BBBBBBBBBBBBBBBBBBBBBBBBBBB" + b);
//		if(a != null) {
//			return a;
//		}else {
//			return b;
//		}
		List<ChattingEntity> all = new ArrayList<>();
		all.addAll(a);
		all.addAll(b);

		return all;
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

	/*
	 * 개선사항 -- 기존 채팅방 존재 여부 체크 > insert할지 말지 설정해야함 현재 중복체크가 없어서 동일 유저 둘이서 다시 채팅할 경우
	 * 기존 채팅방 불러오기가 아닌 중복된 채팅방이 늘어남
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
		String userA = chattingEntity.getUserId1(); // 누가 누구든 상관 없이 정렬용
		String userB = chattingEntity.getUserId2();

		// 채팅방 중복 방지
		if (userA.compareTo(userB) > 0) {
			chattingEntity.setUserId1(userB);
			chattingEntity.setUserId2(userA);
		}
		// 💡 기존 채팅방 있는지 확인할 때, commissionIdx까지 포함해서 조회
	    Optional<ChattingEntity> existingChatRoom = jpaChattingRepository
	        .findByUserId1AndUserId2AndCommissionIdx(
	            chattingEntity.getUserId1(),
	            chattingEntity.getUserId2(),
	            chattingEntity.getCommissionIdx()
	        );

	 // 있다면 기존 roomIdx 반환, 없다면 새 채팅방 생성
	    return existingChatRoom.map(ChattingEntity::getRoomIdx)
	    		.orElseGet(() -> {
	        ChattingEntity newChat = new ChattingEntity();
	        newChat.setUserId1(chattingEntity.getUserId1());
	        newChat.setUserId2(chattingEntity.getUserId2());
	        newChat.setCommissionIdx(chattingEntity.getCommissionIdx());
	        newChat.setCommissionWriterId(chattingEntity.getCommissionWriterId());
	        return jpaChattingRepository.save(newChat).getRoomIdx();
		});
	}

//		List<ChattingEntity> direct = jpaChattingRepository.findByUserId1AndUserId2(userA, userB);
//		List<ChattingEntity> reverse = jpaChattingRepository.findByUserId1AndUserId2(userB, userA);
//		
//		ChattingEntity newChatRoom = jpaChattingRepository.save(chattingEntity);
//		return newChatRoom.getRoomIdx();

	
	@Override
	@Transactional
	public void updateCommissionWriter(int roomIdx, String newWriterId) {
	    ChattingEntity entity = jpaChattingRepository.findById(roomIdx)
	        .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
	    
	    entity.setCommissionWriterId(newWriterId);
	    jpaChattingRepository.save(entity); // 필수는 아니지만 명시적 저장
	}
	
	/*
	 * 다른 메서드들은 CRUD 중심이라 별다른 처리 로직이 없지만 아래의 메서드는 중복 제거 및 정렬 + 마지막 메세지에 대한 데이터를
	 * 뽑아내야하는 등 여러 과정이 필요하기 때문에 보다 간단 명료하고 효율적인 코드 작성을 위해 (구현을 위해) Stream API와
	 * Comparator 체이닝을 활용함
	 */
	// 기능 특성상 정렬, 중복 제거, DTO 변환이 필요한 메서드라 단순 반복문보다 Stream 체이닝 방식이 더 적합
	@Override
	public List<ChattingRoomLastMessageDto> getChattingRoomMessage(String userId) {

		// - 로그인한 유저가 속한 채팅방 전체 조회 (userId1 또는 userId2가 본인인 경우)
		// (로그인한 유저가 속한 채팅방을 userId1 또는 userId2 기준으로 모두 조회한 후 중복 제거)
		List<ChattingEntity> chatRooms = Stream.concat(
				/*
				 * List.addAll()로 두 리스트를 붙일 수도 있지만 Stream을 쓰면 중간에 filter, map, distinct 등 유연한 중간
				 * 처리들을 체이닝으로 넣을 수 있음
				 */ // 체이닝 - 메서드를 체인처럼 이어서 사용하는 것
				jpaChattingRepository.findByUserId1(userId).stream(), // userId1로 참여한 방
				jpaChattingRepository.findByUserId2(userId).stream() // userId2로 참여한 방
		).distinct() // 동일한 채팅방이 2번 나올 수 있으니 중복제거
				.collect(Collectors.toList());

		// - 각 채팅방에 대해 마지막 메시지 뽑고, DTO로 매핑
		List<ChattingRoomLastMessageDto> result = new ArrayList<>();
		/* chatRooms 리스트를 순회하면서 각 요소를 chatRoom으로 꺼냄 */
		for (ChattingEntity chatRoom : chatRooms) { // chatRooms 리스트 안에있는 ChattingEntity 객체들을 하나씩 꺼내면서 chatRoom 변수에 담고 그
													// chatRoom객체로 반복문을 수행하는 것
			int roomIdx = chatRoom.getRoomIdx(); // 현재 채팅방 index 추출

			// 해당 채팅방(roomIdx)의 모든 메시지를 조회
			// (해당 채팅방의 메시지 중 가장 마지막 메시지 (createdTime 기준으로 가장 마지막))
			List<MessageEntity> messages = jpaMessageRepository.findByRoomIdx(roomIdx);

			// 메시지가 없는 경우를 대비한 기본값 초기화
			String lastMessage = "";
			LocalDateTime lastSentTime = null;

			// 메시지가 하나 이상 있을 때 가장 마지막 메시지 추출
			if (!messages.isEmpty()) {
				MessageEntity last = messages.get(messages.size() - 1); // 마지막 메시지 - 리스트 인덱스는 0부터 시작하므로
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
		result.sort(Comparator.comparing(ChattingRoomLastMessageDto::getLastSentTime, // 클래스명::메서드명 or 인스턴스::메서드명 형태
																						// --람다식 간단히 줄여쓴 문법
				Comparator.nullsLast(Comparator.reverseOrder()))); // Comparator.comparing(dto -> dto.getLastSentTime())
																	// 이걸 간단하게 표현한 것
		// Comparator.comparing(...) - 특정 필드를 기준으로 비교하겠다
		// ChattingRoomLastMessageDto::getLastSentTime - 정렬 기준 필드
		// Comparator.nullsLast(...) - null인 값은 맨 뒤로
		// Comparator.reverseOrder() - 최신순(내림차순) 정렬

		// ChattingRoomLastMessageDto 클래스의 getLastSentTime 메서드 필드를 기준으로 비요하여 null값은 맨 뒤에
		// 두고 최신순으로 정렬

		// getLastSentTime() 값을 기준으로 정렬
		// 만약 lastSentTime이 null이 아니면 시간순으로 비교, null인 경우는 리스트 뒤쪽으로 보냄
		// -> reverseOrder로 최근 시간이 맨 위로 오도록 내림차순 정렬

		return result; // <-- 모든 채팅방에 대해 DTO 리스트 반환
	}
} // <-- GET /api/chatroom/list 요청 시 로그인한 사용자의 모든 채팅방 리스트가 불려옴 > 각 채팅방의 roomIdx,
	// 대화상대, 마지막 메세지, 시간 까지 응답
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

/* stream().forEach() - 단순 반복뿐 아니라 필터링, 변환, 정렬 등을 자유롭게 다룰 수 있음 */

// 기존에는 for-each 문을 사용하여 (채팅목록을) 단순 반복처리했지만 채팅방 중복 생성 및 정렬 이슈가 발생하여
// 이를 개선하기 위해 Stream API와 Comparator 체이닝을 도입하여 기존의 코드를 간결하고 명확하게 개선하였습니다.

// 기존의 for-each문 구성에선 채팅방 중복체크 및 정렬이 제대로 관리되지 않는 문제가 있었으나
// 람다식과 Comparator 활용으로 가독성 높은 최신순 정렬 로직을 구현
/*
 * -- 채팅방 중복 제거 및 최신순 정렬을 위해 Stream API와 Comparator 체이닝을 도입(사용)하여 기존 for-each
 * 기반의 코드를 간결하고 명확하게 (처리)개선했습니다
 */
