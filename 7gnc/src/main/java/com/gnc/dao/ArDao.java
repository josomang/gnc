package com.gnc.dao;

import java.time.LocalDate;
import java.util.ArrayList;


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
	
	public void arRegisterDao(String USE_DT, String AR_CN_TYPE,String AR_USER_CAT, double DGSTFN_SCORE,LocalDate lOG_CRT_YMD,String GENDER,String ADDRESS);
	
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
	
	
	public int getCountday01(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday02(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday03(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday04(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday05(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday06(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday07(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday08(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday09(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday10(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday11(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday12(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday13(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday14(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday15(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday16(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday17(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday18(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday19(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday20(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday21(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday22(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday23(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday24(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday25(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday26(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday27(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday28(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday29(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public int getCountday30(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	public Integer getCountday31(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	public Integer arStatisticsSum(@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	public Integer arUseStatisticsSum(@Param("n")int n,@Param("LOG_CRT_YMD") String LOG_CRT_YMD);
	
	public ArrayList<String> arAllList();
	

	
	
	
	
	
	
	
	
}
