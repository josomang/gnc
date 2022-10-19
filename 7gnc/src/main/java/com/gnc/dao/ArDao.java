package com.gnc.dao;

import java.time.LocalDate;
import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArDao {
	public int getCount01 (@Param("year") String year);
	
	public int getCount02 (@Param("year") String year);
	
	public int getCount11 (@Param("year") String year);
	
	public int getCount12 (@Param("year") String year);
	
	public int sum01 (@Param("year") String year);
	
	public int sum02 (@Param("year") String year);
	
	public int sum11 (@Param("year") String year);
	
	public int sum12 (@Param("year") String year);
	
	public int getCountAll (@Param("year") String year);
	
	public int sumAll (@Param("year") String year);
	
	public void arRegisterDao(String USE_DT, String AR_CONTENT_TYPE,String AR_USER_CATEGORY, int DGSTFN_SCORE,LocalDate lOG_CRT_YMD);
	
	public int getCount01_10(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount01_11(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount01_12(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount01_13(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount01_14(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount01_15(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount01_16(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount01_17(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	public int getCount02_10(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount02_11(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount02_12(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount02_13(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount02_14(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount02_15(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount02_16(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount02_17(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	public int getCount11_10(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount11_11(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount11_12(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount11_13(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount11_14(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount11_15(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount11_16(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount11_17(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	public int getCount12_10(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount12_11(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount12_12(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount12_13(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount12_14(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount12_15(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount12_16(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCount12_17(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	
	
	
	
	
}
