package bridge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.NoticeDto;

@Mapper
public interface NoticeMapper {

	List<NoticeDto> noticeList() throws Exception;
	NoticeDto noticeDetail(int noticeIdx) throws Exception;
	void insertNotice(NoticeDto noticeDto)throws Exception;
	NoticeDto selectNoticeDetail(int noticeIdx)throws Exception;
	int updateNotice(NoticeDto noticeDto)throws Exception;
	int deleteNotice(int noticeIdx)throws Exception;
	int selectNoticeListCount() throws Exception;
//	List<NoticeDto> selectNoticeList(int offset) throws Exception;
//	List<NoticeDto> getSerchList(String search)throws Exception;
}
