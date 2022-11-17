package com.gnc.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.LessonsDto;
import com.gnc.dto.goalsDto;

@Mapper
public interface UserDao {
	// 로그인
    public String getUserAccount(@Param("UID")String UID,@Param("PSWD") String PSWD);
    
    public String suerveyGetUserAccount(@Param("UID")String UID,@Param("PSWD") String PSWD);
    
    public String suerveyCenterId(@Param("UID")String UID,@Param("PSWD") String PSWD);
    
    
    public void last_lgn_dtDao
    (@Param("UID")String UID,@Param("LAST_LGN_DT")LocalDateTime last_lgn_dt);
    
    public void use_dtDao
    (@Param("UID")String UID,@Param("USE_DT")LocalDateTime USE_DT);
    
    public void goalRegisterDao(String a,String b ,String c ,String d,String e,int f,String year);
    
    public List<goalsDto> goalDao(@Param("year")String year);
}
