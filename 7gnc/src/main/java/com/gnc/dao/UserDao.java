package com.gnc.dao;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {
	// 로그인
    public String getUserAccount(@Param("UID")String UID,@Param("PSWD") String PSWD);
    
    public void last_lgn_dtDao
    (@Param("UID")String UID,@Param("LAST_LGN_DT")LocalDateTime last_lgn_dt);
    
    public void use_dtDao
    (@Param("UID")String UID,@Param("USE_DT")LocalDateTime USE_DT);
}
