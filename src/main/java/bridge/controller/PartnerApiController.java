package bridge.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bridge.dto.ComposerRequestDto;
import bridge.dto.ComposerRequestTagDto;
import bridge.mapper.BridgeMapper;
import bridge.service.BridgeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Slf4j
public class PartnerApiController {
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	BridgeService bridgeService;

	@Autowired
	BridgeMapper bridgeMapper;

	
	@Operation(summary="커미션 게시글 작성")
	@PostMapping("/api/insertPartnerWrite")
	public ResponseEntity<Map<String, Object>> insertPartnerWrite(
			@RequestPart(value = "data", required = false) ComposerRequestDto composerRequestDto,
			@RequestPart(value = "files", required = false) MultipartFile[] files) throws Exception {

		ComposerRequestTagDto dto = new ComposerRequestTagDto();
		dto.setCrIdx(composerRequestDto.getCrIdx());
		dto.setCrtTags(composerRequestDto.getCrtTag());

//		String UPLOAD_PATH = "C:/home/ubuntu/temp/"; -- 로컬경로로 수정(임시)
		String UPLOAD_PATH = "C:/Users/조아라/files/"; 
		int insertedCount = 0;

		String fileNames = "";
		try {
			for (MultipartFile mf : files) {
				String originFileName = mf.getOriginalFilename(); // 원본 파일 명
				long fileSize = mf.getSize(); // 파일 사이즈
				String saveFile = System.currentTimeMillis() + originFileName;
				composerRequestDto.setCrPhoto(originFileName); // 원본 파일명으로 crPhoto set
				fileNames = fileNames + "," + saveFile;
				try {
					File f1 = new File(UPLOAD_PATH + originFileName);
					mf.transferTo(f1);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}

			insertedCount = bridgeService.insertPartnerWrite(composerRequestDto, files); // 서비스 실행

			dto.setCrIdx(composerRequestDto.getCrIdx());
			bridgeService.insertCrtTag(dto);

			if (insertedCount > 0) {
				Map<String, Object> result = new HashMap<>();
				result.put("message", "정상적으로 등록되었습니다.");
				result.put("count", insertedCount);
				return ResponseEntity.status(HttpStatus.OK).body(result);
			} else {
				Map<String, Object> result = new HashMap<>();
				result.put("message", "등록된 내용이 없습니다.");
				result.put("count", insertedCount);
				return ResponseEntity.status(HttpStatus.OK).body(result);
			}
		} catch (Exception e) {
			Map<String, Object> result = new HashMap<>();
			System.out.println(e);
			result.put("message", "등록 중 오류가 발생했습니다.");
			result.put("count", insertedCount);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}

	@Operation(summary="커미션 목록 조회")
	@GetMapping("/api/openPartnerList")
	public ResponseEntity<Map<String, Object>> openPartnerList() throws Exception {

		Map<String, Object> map = new HashMap<>();
		List<ComposerRequestDto> list = bridgeService.openPartnerList();
		map.put("partnerList", list);

		if (list == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
	};

	@Operation(summary="커미션 게시글에 달린 태그 조회")
	@GetMapping("/api/openTagList")
	public ResponseEntity<Map<String, Object>> openTagList() throws Exception {
		Map<String, Object> map = new HashMap<>();
		List<ComposerRequestTagDto> tagList = bridgeService.partnerTagList();
		map.put("partnerTag", tagList);

		if (tagList == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
	}

	@Operation(summary="커미션 게시글 상세 조회")
	@GetMapping("/api/openPartnerDetail/{crIdx}")
	public ResponseEntity<Map<String, Object>> openPartnerDetail(@PathVariable("crIdx") int crIdx) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ComposerRequestDto composerRequestDto = bridgeMapper.openPartnerDetail(crIdx);
		List<ComposerRequestTagDto> composerRequestTagDto = bridgeMapper.PartnerDetailTag(crIdx);
		map.put("partnerList", composerRequestDto);
		map.put("partnerTag", composerRequestTagDto);

		if (composerRequestDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}

	}

	// 이미지 불러오는 코드 -> 재활용 가능
//	@Operation(summary="커미션 목록 이미지 조회")
//	@GetMapping("/api/getImage/{imgName}")
//	public void getImage(@PathVariable("imgName") String imgName, HttpServletResponse response) throws Exception {
//		FileInputStream fis = null;
//		BufferedInputStream bis = null;
//		OutputStream out = null;
//		String UPLOAD_PATH = "C:/Users/조아라/files/"; 
////		String UPLOAD_PATH = "C:/home/ubuntu/temp/";
//
//		try {
//			response.setContentType("image/jpeg");
//			fis = new FileInputStream(UPLOAD_PATH + imgName);
//			bis = new BufferedInputStream(fis);
//			out = response.getOutputStream();
//
//			byte[] buffer = new byte[1024];
//			while (bis.read(buffer) != -1) {
//				out.write(buffer);
//			}
//		} finally {
//			if (bis != null) {
//				bis.close();
//			}
//			if (out != null) {
//				out.close();
//			}
//			if (fis != null) {
//				fis.close();
//			}
//		}
//	}
	
//	@Operation(summary = "커미션 목록 이미지 조회")
//	@GetMapping("/api/getImage/{imgName}")
//	public void getImage(@PathVariable("imgName") String imgName, HttpServletResponse response) throws Exception {
//	    String UPLOAD_PATH = "C:/Users/조아라/files/"; 
//	    // String UPLOAD_PATH = "/home/ubuntu/temp/"; // 서버일 경우
//
//	    File file = new File(UPLOAD_PATH, imgName);
//
//	    // ✅ 파일 없으면 기본 이미지로 대체
//	    if (!file.exists()) {
//	        file = new File(UPLOAD_PATH, "default.png"); 
//	        if (!file.exists()) {
//	            response.sendError(HttpServletResponse.SC_NOT_FOUND);
//	            return;
//	        }
//	    }
//
//	    response.setContentType("image/jpeg");
//	    try (
//	        FileInputStream fis = new FileInputStream(file);
//	        BufferedInputStream bis = new BufferedInputStream(fis);
//	        OutputStream out = response.getOutputStream()
//	    ) {
//	        byte[] buffer = new byte[1024];
//	        int length;
//	        while ((length = bis.read(buffer)) != -1) {
//	            out.write(buffer, 0, length);
//	        }
//	    }
//	}
	
	@Operation(summary="커미션 게시글 수정")
	@PutMapping("/api/updatePartner/{crIdx}")
	public ResponseEntity<Object> updatePartner(@PathVariable("crIdx") int crIdx,
			@RequestPart(value = "data", required = false) ComposerRequestDto composerRequestDto,
			@RequestPart(value = "files", required = false) MultipartFile[] files) throws Exception {
		bridgeMapper.updatePartner(crIdx);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	@Operation(summary="커미션 게시글 삭제")
	@DeleteMapping("/api/deletePartner/{crIdx}")
	public ResponseEntity<Object> deletePartner(@PathVariable("crIdx") int crIdx) throws Exception {
		bridgeMapper.deletePartner(crIdx);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
}
