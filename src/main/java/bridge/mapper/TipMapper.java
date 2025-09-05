package bridge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import bridge.dto.TipCommentsDto;
import bridge.dto.TipDto;
@Mapper
public interface TipMapper {

	int insertTip(TipDto tipDto);

	TipDto tipdetail(int tbIdx);

	List<TipCommentsDto> tipcommentslist(int tbIdx);

	List<TipDto> tipList();

	void updateViews(int tbIdx);

	void insertComment(TipCommentsDto tipCommentsDto);

	void updateTip(TipDto tipDto);

	void deleteTip(int tbIdx);

	int cancleHeartCount(TipDto tipDto);

	List<TipDto> selectHeartsList();

	int updateHeartCount(TipDto tipDto);

	TipDto selectHeartCount(int tbIdx);

}
