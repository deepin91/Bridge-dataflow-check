package bridge.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bridge.dto.TipCommentsDto;
import bridge.dto.TipDto;
import bridge.dto.UserDto;
import bridge.service.TipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "Tip API", description = "Community 게시글 및 댓글 관련 API")
public class TipApiController {

	
	//------여기 url이랑 메서드 명이랑 다 community로 고치던지 tip으로 고치던지 맞춰야함 
	// 프론트/백엔드/DB 다 tip으로 설정되어있는데 웹에선 community임 혼돈야기 가능
	@Autowired
	TipService tipService;

	@Operation(summary = "community 게시글 작성", description = "로그인한 유저가 새로운 글을 작성")
	@PostMapping("/api/inserttip")
	public ResponseEntity<Object> insertTip(@RequestBody TipDto tipDto, Authentication authentication)
			throws Exception {
		UserDto userDto = (UserDto) authentication.getPrincipal();
		tipDto.setUserId(userDto.getUserId());
		int registedCount = tipService.insertTip(tipDto);
		if (registedCount > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(registedCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(registedCount);
		}

	}

	// update 에 1이 넘어오면 뷰 횟수 증가
	@Operation(summary = "community 게시글 상세 조회", description = "특정 게시글과 댓글 리스트를 조회 -  update=1이면 조회수 증가")
	@GetMapping("/api/tipdetail/{tbIdx}/{update}")
	public ResponseEntity<Map<String, Object>> tipDetail(
			@PathVariable("tbIdx") int tbIdx,
			@PathVariable("update") int update) throws Exception {
		TipDto tipDto = tipService.tipdetail(tbIdx);
		List<TipCommentsDto> tipCommentsDto = tipService.tipcommentslist(tbIdx);
		Map<String, Object> map = new HashMap<>();
		if (update == 1) {
			tipService.updateViews(tbIdx);
		}
		map.put("tipDetail", tipDto);
		map.put("commentsList", tipCommentsDto);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	@Operation(summary = "댓글 작성", description = "로그인된 유저가 팁 게시글에 댓글 작성")
	@PostMapping("/api/comment")
	public ResponseEntity<Object> insertComment(@RequestBody TipCommentsDto tipCommentsDto,
			Authentication authentication) {
		UserDto userDto = (UserDto) authentication.getPrincipal();
		tipCommentsDto.setUserId(userDto.getUserId());
		tipService.insertComment(tipCommentsDto);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	@Operation(summary = "게시글 목록 조회", description = "전체 게시글 리스트를 조회")
	@GetMapping("/api/tiplist")
	public ResponseEntity<List<TipDto>> tiplist() throws Exception {
		List<TipDto> tipDto = tipService.tipList();
		return ResponseEntity.status(HttpStatus.OK).body(tipDto);
	}

	@Operation(summary = "댓글 목록 조회", description = "특정 게시글에 달린 댓글 리스트를 조회")
	@GetMapping("/api/comments/{tbIdx}")
	public ResponseEntity<List<TipCommentsDto>> getComments(@PathVariable("tbIdx") int tbIdx) { // @PathVariable("tbIdx") < --이 줄 tb_idx 로 되어있어서 axios오류 남 -수정 09/30
		List<TipCommentsDto> tipCommentsDto = tipService.tipcommentslist(tbIdx);
		return ResponseEntity.status(HttpStatus.OK).body(tipCommentsDto);
	}

	@Operation(summary = "게시글 수정", description = "기존 게시글 내용을 수정")
	@PutMapping("/api/update/tip")
	public ResponseEntity<Object> updateTip(@RequestBody TipDto tipDto) {
		tipService.updateTip(tipDto);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제")
	@DeleteMapping("/api/tip/delete/{tbIdx}")
	public ResponseEntity<Object> updateTip(@PathVariable("tbIdx") int tbIdx) {
		tipService.deleteTip(tbIdx);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	// 상세 조회 - 좋아요
	@Operation(summary = "좋아요 수 조회", description = "특정 게시글의 좋아요 수를 조회")
	@GetMapping("/api/tipdetail/{tbIdx}/getHeart")
	public ResponseEntity<TipDto> openGetHeart(@PathVariable("tbIdx") int tbIdx) throws Exception {
		TipDto tipDto = tipService.selectHeartCount(tbIdx);
		if (tipDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(tipDto);
		}
	}

	// 좋아요 수 업데이트
	@Operation(summary = "좋아요 증가", description = "특정 게시글의 좋아요 수를 +1 증가")
	@PutMapping("/api/tipdetail/{tbIdx}/heart")
	public ResponseEntity<Integer> updateHeart(@PathVariable("tbIdx") int tbIdx, @RequestBody TipDto tipDto)
			throws Exception {
		int updatedCount = tipService.updateHeartCount(tipDto);
		if (updatedCount != 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updatedCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(updatedCount);
		}
	}

	// 좋아요 수 업데이트
	@Operation(summary = "좋아요 취소", description = "특정 게시글의 좋아요를 취소 (이미 좋아요를 눌렀다면 -1하여 감소)")
	@PutMapping("/api/tipdetail/{tbIdx}/unHeart")
	public ResponseEntity<Integer> cancleHeart(@PathVariable("tbIdx") int tbIdx, @RequestBody TipDto tipDto)
			throws Exception {
		int updatedCount = tipService.cancleHeartCount(tipDto);
		if (updatedCount != 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updatedCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(updatedCount);
		}
	}
	
	
	
//	@GetMapping("/api/tiplist")
//	public ResponseEntity<List<TipDto>> tiplist() throws Exception {
//		List<TipDto> tipDto = tipService.tipList();
//		return ResponseEntity.status(HttpStatus.OK).body(tipDto);
//	}

	// 사용자 : 뮤지컬 메인 화면- 좋아요 랭킹 순 출력
	@Operation(summary = "좋아요 랭킹 순 출력", description = "좋아요가 높은 순으로 정렬된 게시글 목록을 조회")
	@GetMapping("/api/tiplist/heartsList")
	public ResponseEntity<List<TipDto>> heartsList() throws Exception {
		List<TipDto> heartsList = tipService.selectHeartsList();
		if (heartsList != null && heartsList.size() > 0) {
			return ResponseEntity.status(HttpStatus.OK).body(heartsList);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

}