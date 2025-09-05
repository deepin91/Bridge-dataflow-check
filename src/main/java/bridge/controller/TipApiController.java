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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TipApiController {

	@Autowired
	TipService tipService;

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
	@GetMapping("/api/tipdetail/{tbIdx}/{update}")
	public ResponseEntity<Map<String, Object>> tipDetail(@PathVariable("tbIdx") int tbIdx,
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

	@PostMapping("/api/comment")
	public ResponseEntity<Object> insertComments(@RequestBody TipCommentsDto tipCommentsDto,
			Authentication authentication) {
		UserDto userDto = (UserDto) authentication.getPrincipal();
		tipCommentsDto.setUserId(userDto.getUserId());
		tipService.insertComment(tipCommentsDto);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@GetMapping("/api/tiplist")
	public ResponseEntity<List<TipDto>> tiplist() throws Exception {
		List<TipDto> tipDto = tipService.tipList();
		return ResponseEntity.status(HttpStatus.OK).body(tipDto);
	}

	@GetMapping("/api/comments/{tbIdx}")
	public ResponseEntity<List<TipCommentsDto>> getComments(@PathVariable("tb_idx") int tbIdx) {
		List<TipCommentsDto> tipCommentsDto = tipService.tipcommentslist(tbIdx);
		return ResponseEntity.status(HttpStatus.OK).body(tipCommentsDto);
	}

	@PutMapping("/api/update/tip")
	public ResponseEntity<Object> updateTip(@RequestBody TipDto tipDto) {
		tipService.updateTip(tipDto);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@DeleteMapping("/api/tip/delete/{tbIdx}")
	public ResponseEntity<Object> updateTip(@PathVariable("tbIdx") int tbIdx) {
		tipService.deleteTip(tbIdx);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	// 상세 조회 - 좋아요
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