package com.gnc.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.CenterDto;
import com.gnc.dto.Criteria;
import com.gnc.dto.PageDTO;

@Mapper
public interface CenterDao {
	
	
	public void registerDao(String CENTER_TTL, String CENTER_ID,
			String CENTER_UID,String CENTER_PSWD,int CENTER_MAX_NOPE);
	
	public void updateDao(@Param("CENTER_TTL")String CENTER_TTL,@Param("CENTER_ID")String CENTER_ID,
			@Param("CENTER_UID")String CENTER_UID,@Param("CENTER_PSWD")String CENTER_PSWD, 
			@Param("CENTER_MAX_NOPE")int CENTER_MAX_NOPE,@Param("UPDT_DT")LocalDateTime UPDT_DT );
	
	public void updateDao2(@Param("CENTER_TTL")String CENTER_TTL,@Param("CENTER_ID")String CENTER_ID,
			@Param("CENTER_UID")String CENTER_UID,@Param("CENTER_PSWD")String CENTER_PSWD, 
			@Param("CENTER_MAX_NOPE")int CENTER_MAX_NOPE,@Param("UPDT_DT")LocalDateTime UPDT_DT,
			@Param("DEL_DT")String DEL_DT);
	
	public List<String> centerListDao(Criteria cri,PageDTO pageDTO,@Param("pageStart")int start,@Param("perPageNum")int perPageNum);
	
	public List<CenterDto> centerDao(@Param("CENTER_ID")Object CENTER_ID);
	
	public List<String> centerNameDao(@Param("CENTER_ID")Object CENTER_ID);
	
	public int getCount();
	
	public List<String> searchDao(@Param("keyword")Object keyword,Criteria cri,PageDTO pageDTO,@Param("pageStart")int pageStart,@Param("perPageNum")int perPageNum);
	
	public void deleteDao(@Param("DEL_DT")LocalDateTime DEL_DT,@Param("CENTER_ID")String CENTER_ID);
	
	public int getSearchCount (@Param("keyword")Object keyword);
	
	public void centerLessonsInsert(int LESSON_ID,String CENTER_ID,int NOPE);
	
	public Integer centerStatisticsSum(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	public Integer centerUseStatisticsSum(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);

	public Object test();

	public String checkIdDao(@Param("CENTER_ID")String CENTER_ID);
	
	public String checkDao(@Param("CENTER_ID")String CENTER_ID);
	
	public String checkDao2(@Param("LESSON_ID")int LESSON_ID,@Param("CENTER_ID")String CENTER_ID );
	
	public String checkDao3(@Param("LESSON_ID")int LESSON_ID);
	
	public String checkDao4(@Param("LESSON_ID")int LESSON_ID,@Param("CENTER_ID")String CENTER_ID );
	
	public void updateDao3(@Param("NOPE")Integer NOPE,@Param("DGSTFN_SCORE")Integer DGSTFN_SCORE,
			@Param("LESSON_ID")int LESSON_ID,@Param("CENTER_ID")String CENTER_ID );
	
	public void updateDao4(@Param("DGSTFN_SCORE")Integer DGSTFN_SCORE,
			@Param("LESSON_ID")int LESSON_ID,@Param("CENTER_ID")String CENTER_ID );
	
	public void updateDao5(@Param("NOPE")Integer NOPE,
			@Param("LESSON_ID")int LESSON_ID,@Param("CENTER_ID")String CENTER_ID );
	
	public void innsertDao(int LESSON_ID,String CENTER_ID,Integer NOPE,Integer DGSTFN_SCORE);
	
}
