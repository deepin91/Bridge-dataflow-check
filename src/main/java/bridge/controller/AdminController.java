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
	public ResponseEntity<List<ReportDto>> openReportList() throws Exception {
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
			@PathVariable("reportIdx") int reportIdx) throws Exception {
		ReportDto reportDto = bridgeService.openReportDetail(reportIdx);
		if (reportDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(reportDto);
		}
	}

	@Operation(summary = "유저 신고 카운트 조회", description = "특정 사용자가 받은 총 신고 횟수를 조회합니다.")
	@GetMapping("/api/reportCount/{userId}")
	public ResponseEntity<Object> selectReportCount(
			@Parameter(description = "신고 카운트를 조회할 사용자의 ID", required = true)
			@PathVariable("userId") String userId) throws Exception {
		try {
			int a = bridgeMapper.selectReportCount(userId);
			return ResponseEntity.status(HttpStatus.OK).body(a);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@Operation(summary = "신고 처리", description = "특정 사용자에 대한 신고를 처리하여 상태를 변경합니다.")
	@PutMapping("/api/handleReport/{userId}")
	public ResponseEntity<Object> handleReport(
			@Parameter(description = "신고를 처리할 사용자의 ID", required = true)
			@PathVariable("userId") String userId,
			@RequestBody UserDto userDto)
		 // @RequestBody(description = "신고 처리 정보를 담은 DTO 객체", required = true)
			throws Exception {
		try {
			userDto.setUserId(userId);
			bridgeService.handleReport(userDto);
			return ResponseEntity.status(HttpStatus.OK).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

}
