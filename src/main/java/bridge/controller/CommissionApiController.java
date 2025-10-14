package bridge.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bridge.dto.CommissionCommentDto;
import bridge.dto.CommissionDetailDto;
import bridge.dto.CommissionDto;
import bridge.dto.ReviewDto;
import bridge.service.CommissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Slf4j
public class CommissionApiController {

	@Autowired
	CommissionService commissionService;

	@Operation(summary="커미션 목록 조회")
	@GetMapping("/api/getCommissionList/{userId}")
	public ResponseEntity<List<CommissionDto>> getCommissionList(
			@Parameter(description="커미션 목록을 조회할 사용자의 ID", required = true)
			@PathVariable("userId") String userId)
			throws Exception {
		List<CommissionDto> list = commissionService.getCommissionList(userId);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@Operation(summary="커미션 진행 상황 및 예치금 조회")
	@GetMapping("/api/getProgress/{cIdx}")
	public ResponseEntity<List<CommissionDto>> getProgress(
			@Parameter(description="커미션 진행 상황 및 예치금 조회를 위한 커미션 Index", required = true)
			@PathVariable("cIdx") int cIdx) throws Exception {
		List<CommissionDto> list = commissionService.getProgress(cIdx);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@Operation(summary="커미션 디테일 조회")
	@GetMapping("/api/getCommissionDetail/{cIdx}")
	public ResponseEntity<List<CommissionDto>> getCommissionDetail(
			@Parameter(description="커미션 디테일 조회시 필요한 커미션  Index", required = true)
			@PathVariable("cIdx") int cIdx) throws Exception {
		List<CommissionDto> list = commissionService.getCommissionDetail(cIdx);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@Operation(summary="커미션 게시글 등록")
	@PostMapping("/api/insertCommissionDetail/{cIdx}")
	public ResponseEntity<Map<String, Object>> insertCommissionDetail(
			@Parameter(description="커미션 게시글 등록 시 관련된 커미션 내용을(정보를)불러와야 하므로 cIdx", required = true)
			@PathVariable("cIdx") int cIdx,
			@RequestPart(value = "data", required = false) CommissionDetailDto commissionDetail,
			@RequestPart(value = "files", required = false) MultipartFile[] files) throws Exception {
//		String UPLOAD_PATH = "C:/home/ubuntu/temp/";
		String UPLOAD_PATH = "C:/Users/조아라/files/"; // --9/30 로컬경로로 수정
		int insertedCount = 0;
		List<String> fileNames = new ArrayList<>();

		Map<String, Object> result = new HashMap<>();

		try {
			if (files != null && files.length > 0) {
				String uuid = UUID.randomUUID().toString();
				for (MultipartFile mf : files) {
					String originFileName = mf.getOriginalFilename();
					try {
						File f = new File(UPLOAD_PATH + File.separator + uuid + ".mp3");
						mf.transferTo(f);

					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
					fileNames.add(originFileName);
					insertedCount++;
				}
				commissionDetail.setCdFile(uuid);
				result.put("uuid", uuid);
			}

			commissionDetail.setCIdx(cIdx);
			commissionService.insertCommissionDetail(commissionDetail);

			if (insertedCount > 0) {
				result.put("fileNames", fileNames);
			}

			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "파일 업로드 중 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	@Operation(summary="커미션 게시글 수정")
	@PutMapping("/api/editCommissionDetail/{cidx}/{cdIdx}")
	public ResponseEntity<Object> editCommissionDetail(
			@Parameter(description = "커미션의 고유 번호", required = true) @PathVariable("cidx") int cidx, 
			@Parameter(description = "수정할 게시글의 고유 번호", required = true) @PathVariable("cdIdx") int cdIdx,
			// @Parameter(description = "수정할 게시글의 DTO 객체", schema = @Schema(implementation = CommissionDetailDto.class))
			@RequestPart(value = "data", required = false) CommissionDetailDto commissionDetail,
			// @Parameter(description = "게시글에 포함될 파일들", schema = @Schema(type = "string", format = "binary"))
			@RequestPart(value = "files", required = false) MultipartFile[] files) throws Exception {

//		String UPLOAD_PATH = "C:/home/ubuntu/temp/";
		String UPLOAD_PATH = "C:/Users/조아라/files/"; // 로컬경로로 수정
		String uuid = UUID.randomUUID().toString();
		List<String> fileNames = new ArrayList<>();

		try {
			if (files != null && files.length > 0) {
				for (MultipartFile mf : files) {
					String originFileName = mf.getOriginalFilename();
					try {
						File f = new File(UPLOAD_PATH + File.separator + uuid + ".mp3");
						mf.transferTo(f);
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
					fileNames.add(originFileName);
				}
			}

			commissionDetail.setCIdx(cidx);
			commissionDetail.setCdIdx(cdIdx);

			if (fileNames.size() > 0) {
				commissionDetail.setCdFile(uuid);
			}
			commissionService.editCommissionDetail(commissionDetail);
			return ResponseEntity.status(HttpStatus.OK).body(commissionDetail);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
		}
	}

	@Operation(summary="커미션 게시글 삭제")
	@PutMapping("/api/delCommissionDetail/{cdIdx}")
	public ResponseEntity<Object> delCommissionDetail(
			@Parameter(description= "커미션 게시글 삭제를 위한 commition detail Index", required = true)
			@PathVariable("cdIdx") int cdIdx) throws Exception {
		commissionService.delCommissionDetail(cdIdx);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary="커미션 첨부파일 삭제")
	@PutMapping("/api/delCommissionFile/{cdIdx}")
	public ResponseEntity<Object> delCommissionFile(
			@Parameter(description= "커미션 첨부파일 삭제를 위한 commition detail Index", required = true)
			@PathVariable("cdIdx") int cdIdx) throws Exception {
		commissionService.delCommissionFile(cdIdx);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary="커미션 목록 삭제") 
	@PutMapping("/api/delCommissionList/{cIdx}") 
	public ResponseEntity<Object> delCommissionList( 
			@Parameter(description = "삭제할 커미션의 ID", required = true) 
			@PathVariable("cIdx") int cIdx) throws Exception {
		commissionService.delCommissionList(cIdx); 
		return ResponseEntity.status(HttpStatus.OK).body(null); 
		}
	

	@Operation(summary="커미션 진행상황 완료")
	@PutMapping("/api/commissionEnd/{cIdx}")
	public ResponseEntity<Object> commissionEnd(
			@Parameter(description = "완료 처리할 커미션의 ID", required = true)
			@PathVariable("cIdx") int cIdx) throws Exception {
		commissionService.commissionEnd(cIdx);
		commissionService.moneyToUser2(cIdx);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary="커미션 목록 생성")
	@PostMapping("/api/insertCommission/{userId2}")
	public ResponseEntity<Object> insertCommission(
			@Parameter(description = "커미션을 생성할 사용자의 ID", required = true)
			@PathVariable("userId2") String userId2,
			@RequestBody CommissionDto commissionDto) throws Exception {
//			@RequestBody(description = "생성할 커미션의 정보를 담은 DTO 객체", required = true)
		commissionDto.setUserId2(userId2);
		commissionService.insertCommission(commissionDto);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@Operation(summary="커미션 댓글 작성")
	@PostMapping("/api/insert/CommissionComment/{cdIdx}")
	public ResponseEntity<Object> CommissionComment(
			@Parameter(description = "댓글을 작성할 커미션의 ID", required = true)
			@PathVariable("cdIdx") int cdIdx,
			@RequestBody CommissionCommentDto commissionCommentDto) throws Exception {
		commissionCommentDto.setCdIdx(cdIdx);
		int insertCount = commissionService.CommissionComment(commissionCommentDto);
		if (insertCount > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(insertCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(insertCount);
		}
	}

	@Operation(summary="커미션 댓글 조회")
	@GetMapping("/api/get/CommissionComment/{cdIdx}")
	public ResponseEntity<List<CommissionCommentDto>> CommissionComment(
			@Parameter(description= "커미션 댓글 조회를 위한 commition detail Index", required = true)
			@PathVariable("cdIdx") int cdIdx)
			throws Exception {
		List<CommissionCommentDto> list = commissionService.CommissionComment(cdIdx);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@Operation(summary="커미션 첨부파일 다운로드")
	@GetMapping("/api/CommissionDown/{uuid}")
	public void CommissionDown(
			@Parameter(description = "커미션 첨부파일 다운로드 관련 UUID", required = true)
			@PathVariable("uuid") String uuid, HttpServletResponse response) throws Exception {
//		String filePath = "C:/home/ubuntu/temp/" + uuid + ".mp3";
		String filePath = "C:/Users/조아라/files/" + uuid + ".mp3";
		File file = new File(filePath);
		if (file.exists()) {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + uuid + "\"");
			try (FileInputStream inputStream = new FileInputStream(file);
					ServletOutputStream outputStream = response.getOutputStream()) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
				outputStream.flush();
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@Operation(summary="커미션 리뷰 작성")
	@PostMapping("/api/insertReview/{userId}")
	public ResponseEntity<Object> insertReview(
			@Parameter(description = "리뷰를 작성할 사용자의 ID", required = true)
			@PathVariable("userId") String userId, @RequestBody ReviewDto reviewDto)
			throws Exception {
		reviewDto.setUserId(userId);
		int insertCount = commissionService.insertReview(reviewDto);
		if (insertCount > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(insertCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(insertCount);
		}
	}

}
