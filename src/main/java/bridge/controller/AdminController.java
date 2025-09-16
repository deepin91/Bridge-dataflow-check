package bridge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bridge.dto.ReportDto;
import bridge.dto.UserDto;
import bridge.mapper.BridgeMapper;
import bridge.service.BridgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Slf4j
public class AdminController {

	@Autowired
	private BridgeService bridgeService;

	@Autowired
	private BridgeMapper bridgeMapper;

	@Operation(summary = "신고 목록 조회", description = "모든 사용자의 신고 목록을 최신순으로 조회합니다.")
	@GetMapping("/api/openReportList")
	/* 여러개의 객체를 담은 리스트를 HTTP형태로 리턴함. 응답에 본문 데이터 + HTTP 상태코드가 포함됨 */
	// List는 여러 개를 담기 위한 Java 컬렉션, <타입>은 그 안에 들어갈 값의 종류(클래스)를 지정해주는 제네릭
	public ResponseEntity<List<ReportDto>> openReportList() throws Exception { // ResponseEntity<List<ReportDto>> (반환 타입 문법형식의 표준 문법)-- openReportList()는 메서드 이름
		List<ReportDto> list = bridgeService.openReportList();
		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(list);
		}
	}

	@Operation(summary = "특정 신고 목록 조회",  description = "특정 신고 ID에 해당하는 신고의 상세 정보를 조회합니다.")
	@GetMapping("/api/openReportDetail/{reportIdx}")
	public ResponseEntity<ReportDto> openReportDetail(
			@Parameter(description = "상세 조회를 위한 신고의 고유 ID", required = true)
			@PathVariable("reportIdx") int reportIdx) throws Exception { // @PathVariable("reportIdx"): URL 경로 {reportIdx} 값을 변수 reportIdx에 바인딩
		ReportDto reportDto = bridgeService.openReportDetail(reportIdx); // 그 값은 정수 타입임
		if (reportDto == null) { // 없으면 → `null` 리턴
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else { // DB에서 해당 신고가 있으면 → `ReportDto` 객체 리턴
			return ResponseEntity.status(HttpStatus.OK).body(reportDto);
		}
	}
	// 특정 유저의 신고 받은 횟수 조회
	@Operation(summary = "유저 신고 카운트 조회", description = "특정 사용자가 받은 총 신고 횟수를 조회합니다.")
	@GetMapping("/api/reportCount/{userId}")
						// ↓ <Object>대신 ResponseEntity<Integer>로 리턴타입 좀 더 확실하게 변경하는 게 좋을지도
	public ResponseEntity<Object> selectReportCount( //Integer나 다른 타입을 넣을 수 있도록 <Object>로 설정
			@Parameter(description = "신고 카운트를 조회할 사용자의 ID", required = true)
			@PathVariable("userId") String userId) throws Exception { // /api/reportCount/{userId} 이 URL 경로에서 {userId} 값을 받아서 userId 변수에 매핑 - String타입
		try {
			int a = bridgeMapper.selectReportCount(userId); //DB와 연결된 Mapper(`bridgeMapper`)의 `selectReportCount` 메서드를 호출   
			return ResponseEntity.status(HttpStatus.OK).body(a); // -> 특정 userId가 몇 번 신고되었는지 DB에서 SELECT COUNT(*) 같은 쿼리 실행 -> 그 결과(정수)를 `a`에 저장 //body에 숫자(신고 횟수) a 담아서 반환
		} catch (Exception e) { //실행 중 예외가 발생하면
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0); //HTTP 상태코드 500 반환 + 실패시 기본값인 0을 body에 담음
		}
	}
	/* ResponseEntity<Integer>로 수정하는 편이 더 명확함 <Object>도 사용가능하고 유연성이 좋으나 리턴타입이 하나라면 전자가 실용적
	 * + body(0) 이 부분도 실패 시 0 대신 "message": "DB error" 같은 JSON으로 응답하는 편이 직관적임 
	 */

	
	// 특정 유저의 신고 처리(상태 변경) API
	@Operation(summary = "신고 처리", description = "특정 사용자에 대한 신고를 처리하여 상태를 변경합니다.")
	@PutMapping("/api/handleReport/{userId}")
	public ResponseEntity<Object> handleReport( //URL 경로 {userId} 값을 받아옴 - ex) /api/handleReport/kim123 → userId = "kim123"
			@Parameter(description = "신고를 처리할 사용자의 ID", required = true)
			@PathVariable("userId") String userId,
			@RequestBody UserDto userDto) // 요청 본문(JSON) 데이터를 UserDto 객체로 자동 변환 --프론트에서 보낸 JSON이 userDto안에 담김
		 // @RequestBody(description = "신고 처리 정보를 담은 DTO 객체", required = true)
			throws Exception {
		try {
			userDto.setUserId(userId); // URL에서 받은 `userId`를 `userDto`에 강제로 세팅 -- 요청 본문에 userId 없어도 → URL PathVariable 값이 최종 userId로 들어감 --(URL과 body가 다르게 오는 상황 방지)--데이터 일관성 보장위해
			bridgeService.handleReport(userDto); //Service 계층의 handleReport() 호출 --> Mapper → UPDATE 쿼리 실행 > DB에서 해당 userId의 신고 상태 변경
			return ResponseEntity.status(HttpStatus.OK).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0); // 상태 변경 처리 중 오류 발생 시 0 뜨며 실패
		}
	}
	/* 이 또한 ResponseEntity<Integer>로 확실히 하거나 공통응답용 제네릭 DTO 하나 만들어서 success + message + data 구조로 응답시키는 방법도 고려 
	 * 0 또는 1 대신 직관적이고 가독성있게 - 하나하나 응답 설정하기보다 ApiResponse<T> / CommonResponse<T> 사용해서 모든 컨트롤러에서 공통으로 반영 후 반환하도록 설정하는 방법도 있음 추천.
	 */
}
