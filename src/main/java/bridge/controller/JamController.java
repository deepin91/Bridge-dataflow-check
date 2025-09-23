package bridge.controller;

import java.io.File;
import java.io.IOException;
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
import io.swagger.v3.oas.annotations.Operation;

@RestController
public class JamController {

	@Autowired
	JamService jamService;
	@Autowired
	BridgeService bridgeService;

	@Operation(summary = "잼 목록 조회")
	@GetMapping("/api/jam")
	public ResponseEntity<List<ConcertDto>> JamList() throws Exception {
		List<ConcertDto> list = jamService.jamList();
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	/* JamList()메서드는 ConcertDto타입(객체)의 List형식으로 응답
	 * ConcertDto타입의 객체 List - list에 jamList()메서드에서 호출, 반환된 값을 저장
	 * 그 값을 body에 담아 상태코드 200 OK와 함께 리턴
	 */

	// -- 9/23
	/* !-- 9/23 - 프론트에서는 JamWrite 글에 대표 이미지 1개만 사용되는 반면 백엔드 로직에서는 다중파일 업로드 형식으로 작성되어
	 * 불필요한 반복 및 코드가 복잡해짐 >  코드 복잡  -  불필요한 반복 제거로 간소화.
	 */
	// !!!!!!!!!!!근데 오류남 이 부분 다시 설정 및 체크 필요 그리고 insertTip()였던 부분도 
	
	/* 잼(Jam) 게시글 작성 시, 파일 업로드 및 로그인 사용자 정보 기반으로 게시글을 등록하는 API */
	@Operation(summary = "잼 게시글 작성") // Swagger 문서에 "잼 게시글 작성"으로 표시
	@PostMapping("/api/insertjam") // 경로로 들어오는 요청을 처리
	public ResponseEntity<Integer> insertJam( // -- 응답으로 상태코드 + int 타입 데이터 (concertDto.getCIdx(), 즉 생성된 게시글 번호)를 보냄
			@RequestPart(value = "data", required = false) ConcertDto concertDto, // "data" 파트는 ConcertDto 형태로 받아옴 (게시글 데이터)
			@RequestPart(value = "file", required = false) MultipartFile file, /*  MultipartFile[] → MultipartFile 로 변경 */
			Authentication authentication) throws Exception { // "files"는 Multipart 형식 파일 배열로 받아옴 (이미지 등)
																							  // Authentication authentication: 로그인한 사용자 정보를 Spring Security로부터 받아옴.
		// 파일 업로드 경로 및 UUID 설정
//		String UPLOAD_PATH = "C:/home/ubuntu/temp/";
		String UPLOAD_PATH = "C:/Users/조아라/files/";
		String uuid = UUID.randomUUID().toString(); // uuid -- 저장할 파일명 중복 방지를 위한 랜덤한 고유 문자열 생성
//		List<String> fileNames = new ArrayList<>(); // 파일명 목록 저장용 리스트
//		Map<String, Object> result = new HashMap<>(); // 결과 저장용 Map
//		int registedCount = 0; // -- 등록 성공 여부를 저장하는 변수 성공 - 1 / 실패 - 0
		
		try { // <--- 여기서부터 파일 업로드 + 게시글 등록 로직
			UserDto userDto = (UserDto) authentication.getPrincipal(); // 현재 로그인한 사용자의 정보를 가져옴
			concertDto.setCWriter(userDto.getUserId()); // 로그인한 사용자 ID를 ConcertDto의 작성자로 지정
			
			// 파일이 첨부된 경우에만 처리
			if ( file != null && !file.isEmpty()) { 
				String originFileName = file.getOriginalFilename(); // --원본 파일 이름 추출
				
				// 확장자 추출
				String extension = "";
				int dotIndex = originFileName.lastIndexOf(".");
				if(dotIndex != -1) {
					extension = originFileName.substring(dotIndex); // .jpg, .png 등
				}
				
				// 저장 파일명: uuid + 확장자
				String savedFileName = uuid + extension;
				File targetFile = new File(UPLOAD_PATH + File.separator + savedFileName);
				
				// 실제 파일을 서버에 저장
				try {
					file.transferTo(targetFile); 
				} catch (IOException | IllegalStateException e) { // 예외 처리 (파일 저장 중 오류 발생 시)
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
				}
				// DB에 저장할 파일명 등록
				concertDto.setCPhoto(savedFileName);
			}
			// 게시글 등록
			int registedCount = jamService.insertJam(concertDto);

			if (registedCount > 0) { // 성공 / 실패여부 - 응답처리
				return ResponseEntity.status(HttpStatus.OK).body(concertDto.getCIdx()); // 등록 성공 시 200 OK 상태와 함께 게시글 번호(CIdx) 응답
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 등록 실패 시 400 Bad Request 응답
			}
			
		} catch (Exception e) { // 코드 전체에서 오류가 발생하면 500 상태로 응답
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	// -- 게시글 첨부파일 등록 시 파일 하나가 아닌 여러 파일을 업로드할 경우 (전부 같은 uuid로 등록되서 마지막 업로드하는 파일에 덮어쓰기되므로) 덮쓰됨
	// 
	

	@Operation(summary = "잼 게시글 음악 첨부")
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
				concertMusicDto.setMusicTitle(originFileName);
//				concertMusicDto.setCmMusic(uuid);
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

	@Operation(summary = "잼 디테일 조회")
	@GetMapping("/api/jam/{cIdx}")
	public ResponseEntity<Map<String, Object>> insertJam(@PathVariable("cIdx") int cIdx) throws Exception {
		ConcertDto Data = jamService.getJam(cIdx);
		List<MusicDto> music = jamService.getMusicUUID(cIdx);
		List<CommentsDto> list = bridgeService.selectCommentsList(cIdx);
		Map<String, Object> result = new HashMap<>();
		result.put("data", Data);
		result.put("music", music);
		result.put("commentsList", list);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@Operation(summary = "잼 댓글 작성")
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

	@Operation(summary = "잼 댓글 삭제")
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
