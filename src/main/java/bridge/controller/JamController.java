package bridge.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bridge.dto.CommentsDto;
import bridge.dto.ConcertDto;
import bridge.dto.ConcertMusicDto;
import bridge.dto.MusicDto;
import bridge.dto.UserDto;
import bridge.service.BridgeService;
import bridge.service.JamService;
import io.swagger.annotations.ApiOperation;

@RestController
public class JamController {

	@Autowired
	JamService jamService;
	@Autowired
	BridgeService bridgeService;

	@ApiOperation(value = "잼 목록 조회")
	@GetMapping("/api/jam")
	public ResponseEntity<List<ConcertDto>> JamList() throws Exception {
		List<ConcertDto> list = jamService.jamList();
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@ApiOperation(value = "잼 게시글 작성")
	@PostMapping("/api/insertjam")
	public ResponseEntity<Integer> insertTip(@RequestPart(value = "data", required = false) ConcertDto concertDto,
			@RequestPart(value = "files", required = false) MultipartFile[] files, Authentication authentication)
			throws Exception {
		String UPLOAD_PATH = "C:/home/ubuntu/temp/";
		String uuid = UUID.randomUUID().toString();
		List<String> fileNames = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();
		int registedCount = 0;
		try {
			for (MultipartFile mf : files) {
				String originFileName = mf.getOriginalFilename();
				try {
					File f = new File(UPLOAD_PATH + File.separator + uuid + ".jpg");
					mf.transferTo(f);

				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				fileNames.add(originFileName);

				UserDto userDto = (UserDto) authentication.getPrincipal();
				concertDto.setCWriter(userDto.getUserId());
				concertDto.setCPhoto(uuid);
				registedCount = jamService.insertJam(concertDto);
			}

			if (registedCount > 0) {

				return ResponseEntity.status(HttpStatus.OK).body(concertDto.getCIdx());
			} else {

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@ApiOperation(value = "잼 게시글 음악 첨부")
	@PostMapping("/api/insertmusic/{cIdx}")
	public ResponseEntity<Integer> insertMusic(@PathVariable("cIdx") int cIdx,
			@RequestPart(value = "data", required = false) ConcertMusicDto concertMusicDto,
			@RequestPart(value = "files", required = false) MultipartFile[] files, Authentication authentication)
			throws Exception {
		String UPLOAD_PATH = "C:/home/ubuntu/temp/";
		String uuid = UUID.randomUUID().toString();
		List<String> fileNames = new ArrayList<>();
		int registedCount = 0;
		try {
			for (MultipartFile mf : files) {
				String originFileName = mf.getOriginalFilename();
				try {
					File f = new File(UPLOAD_PATH + File.separator + uuid + ".mp3");
					mf.transferTo(f);

				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				fileNames.add(originFileName);

				UserDto userDto = (UserDto) authentication.getPrincipal();
				concertMusicDto.setCmUser(userDto.getUserId());
				concertMusicDto.setMusicUUID(uuid);
				concertMusicDto.setMucisTitle(originFileName);
				concertMusicDto.setCmMusic(uuid);
				concertMusicDto.setCIdx(cIdx);
				registedCount = jamService.insertMusic(concertMusicDto);
			}

			if (registedCount > 0) {

				return ResponseEntity.status(HttpStatus.OK).body(null);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@ApiOperation(value = "잼 디테일 조회")
	@GetMapping("/api/jam/{cIdx}")
	public ResponseEntity<Map<String, Object>> insertTip(@PathVariable("cIdx") int cIdx) throws Exception {
		ConcertDto Data = jamService.getJam(cIdx);
		List<MusicDto> music = jamService.getMusicUUID(cIdx);
		List<CommentsDto> list = bridgeService.selectCommentsList(cIdx);
		Map<String, Object> result = new HashMap<>();
		result.put("data", Data);
		result.put("music", music);
		result.put("commentsList", list);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@ApiOperation(value = "잼 댓글 작성")
	@PostMapping("/api/insertComments/{cIdx}")
	public ResponseEntity<Map<String, Object>> insertComments(@RequestBody CommentsDto commentsDto,
			@PathVariable("cIdx") int cIdx, Authentication authentication) throws Exception {
		Map<String, Object> result = new HashMap<>();
		UserDto userDto = (UserDto) authentication.getPrincipal();
		commentsDto.setUserId(userDto.getUserId());
		int insertedCount = 0;
		commentsDto.setCIdx(cIdx);
		insertedCount = bridgeService.insertComments(commentsDto);
		if (insertedCount > 0) {
			result.put("message", "정상적으로 등록되었습니다.");
			result.put("ccIdx", commentsDto.getCcIdx());

			return ResponseEntity.status(HttpStatus.OK).body(result);
		} else {
			result.put("message", "등록된 내용이 없습니다.");
			result.put("count", insertedCount);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
		}
	}

	@ApiOperation(value = "잼 댓글 삭제")
	@DeleteMapping("/api/CommentsDelete/{ccIdx}")
	public ResponseEntity<Object> deleteComments(@PathVariable("ccIdx") int ccIdx) throws Exception {
		try {
			bridgeService.deleteComments(ccIdx);
			return ResponseEntity.status(HttpStatus.OK).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}
}
