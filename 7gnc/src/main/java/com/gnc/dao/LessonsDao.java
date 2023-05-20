package com.gnc.dao;


import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.ArDto;
import com.gnc.dto.Criteria;
import com.gnc.dto.LessonsDto;
import com.gnc.dto.lect_listDto;



@Mapper
public interface LessonsDao {
	public void registerDao(String LESSON_TTL, String BGNG_DT,String END_DT, 
			String LESSON_TM,String ROOM_ID,
			String INSTR_NM, String LESSON_TYPE,String LESSON_DESC);
	
	public void updateDao(@Param("LESSON_ID")int LESSON_ID,@Param("LESSON_TTL")
	String LESSON_TTL,
			@Param("BGNG_DT")String BGNG_DT,@Param("END_DT")String END_DT,
			@Param("LESSON_TM")String LESSON_TM,@Param("ROOM_ID")String ROOM_ID,
			@Param("INSTR_NM")String INSTR_NM,
			@Param("LESSON_TYPE")String LESSON_TYPE,@Param("LESSON_DESC")
	String LESSON_DESC
			,@Param("UPDT_DT")LocalDateTime UPDT_DT);
	
	public void surveyUpdateDao(@Param("DGSTFN_SCORE")int DGSTFN_SCORE,@Param("LESSON_CENTER_ID")
	int LESSON_CENTER_ID);
	
	public List<String> lessonsListDao(Criteria cri);
	
	public List<String> surveyLessonsListDao(Criteria cri,@Param("CENTER_ID")Object CENTER_ID);
	
	
	public List<String> searchDao(@Param("keyword")Object keyword,Criteria cri,@Param("pageStart")int pageStart,@Param("perPageNum")int perPageNum );
	
	public List<String> centerLessonsListDao(@Param("LESSON_ID")int LESSON_ID);
	
	public List<String> centerTTListDao();
	
	public List<LessonsDto> lessonsDao(@Param("LESSON_ID")Object LESSON_ID);
	
	public int getCount ();
	
	public int getSearchCount (@Param("keyword")Object keyword);
	
	public int getIdDao(int LESSON_ID);
	
	public List<String> TtlDao(@Param("LESSON_CENTER_ID") int LESSON_CENTER_ID);
	
	public void deleteDao(@Param("DEL_DT")LocalDateTime DEL_DT,@Param("LESSON_ID")int LESSON_ID);
	
	public List<String> test();
	
	public Integer totalCountDao(@Param("LESSON_ID")int LESSON_ID);
	
	public Integer centerCountDao(@Param("LESSON_ID")int LESSON_ID);
	
	public Integer surveyDao(@Param("LESSON_ID")int LESSON_ID);
	
	public  List<lect_listDto> lect_listDao();
	
	
	
    public List<Map<String,Integer>> arCountDao();
    
    public List<ArDto> arDayDao(@Param("day")String day);
    
    public int arMonthCountDao(@Param("month") String month);
    
    public int getCountAll (@Param("year") String year);
    
    public int sumAll (@Param("year") String year);
    
}

