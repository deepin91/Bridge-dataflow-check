package bridge.service;

import java.util.List;

import bridge.dto.TipCommentsDto;
import bridge.dto.TipDto;

public interface TipService {

	int insertTip(TipDto tipDto);

	TipDto tipdetail(int tbIdx);

	List<TipCommentsDto> tipcommentslist(int tbIdx);

	List<TipDto> tipList();

	void updateViews(int tbIdx);

	void insertComment(TipCommentsDto tipCommentsDto);

	void updateTip(TipDto tipDto);

	void deleteTip(int tbIdx);

	TipDto selectHeartCount(int tbIdx);

//	int updateHeartCount(int tbHeart);

	int updateHeartCount(TipDto tipDto);

	int cancleHeartCount(TipDto tipDto);

	List<TipDto> selectHeartsList();

}