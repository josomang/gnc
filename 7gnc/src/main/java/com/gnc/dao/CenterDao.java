package com.gnc.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.CenterDto;
import com.gnc.dto.Criteria;

@Mapper
public interface CenterDao {
	
	
	public void registerDao(String CENTER_TTL, String CENTER_ID,
			String CENTER_UID,String CENTER_PSWD,int CENTER_MAX_NOPE);
	
	public void updateDao(@Param("CENTER_TTL")String CENTER_TTL,@Param("CENTER_ID")String CENTER_ID,
			@Param("CENTER_UID")String CENTER_UID,@Param("CENTER_PSWD")String CENTER_PSWD, 
			@Param("CENTER_MAX_NOPE")int CENTER_MAX_NOPE,@Param("UPDT_DT")LocalDateTime UPDT_DT );
	
	public List<String> centerListDao(Criteria cri);
	
	public List<CenterDto> centerDao(@Param("CENTER_ID")Object CENTER_ID);
	
	public int getCount ();
	
	public List<String> searchDao(@Param("keyword")Object keyword,Criteria cri);
	
	public void deleteDao(@Param("CENTER_ID")String CENTER_ID);
	
	public int getSearchCount (@Param("keyword")Object keyword);
	

}
