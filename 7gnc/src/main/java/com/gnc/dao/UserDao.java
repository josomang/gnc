package com.gnc.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import com.gnc.dto.goalsDto;

@Mapper
public interface UserDao {
	// 로그인
    public String getUserAccount(@Param("UID")String UID,@Param("PSWD") String PSWD);
    
    public String suerveyGetUserAccount(@Param("UID")String UID,@Param("PSWD") String PSWD);
    
    public String suerveyCenterId(@Param("UID")String UID,@Param("PSWD") String PSWD);
    
    
    public void last_lgn_dtDao
    (@Param("ADMIN_ID")String ADMIN_ID,@Param("LAST_LGN_DT")LocalDateTime last_lgn_dt);
    
    public void use_dtDao
    (@Param("ADMIN_ID")String ADMIN_ID,@Param("USE_DT")LocalDateTime USE_DT);
    
    public void goalRegisterDao(String a,String b ,String c ,String d,String e,int f,int year);
    
    public void goalUpdateDao(@Param("AR_DGSTFN_GOAL")String a,@Param("LESSON_DGSTFN_GOAL")String b ,
    		@Param("AR_RTOFUT_GOAL")String c ,@Param("LESSON_RTOFUT_GOAL")String d,@Param("LIBRARY_USER_RTOFINC_GOAL")String e,@Param("LIBRARY_NUOFUS")int f,
    		@Param("TRGT_YR")int year);
    
    public List<goalsDto> goalDao(@Param("year")String year);
    
    
    public Integer arStatisticsSum(@Param("year")String year);
    
    public Integer centerStatisticsSum(@Param("year")String year);
    
    public Integer getLibraryPeopleDao(@Param("year")String year);
    
    public Integer arUseStatisticsSum(@Param("year")String year);
    
    public Integer centerUseStatisticsSum(@Param("year")String year);
    
    
}
