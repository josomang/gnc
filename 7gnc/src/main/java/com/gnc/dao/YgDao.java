package com.gnc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.Criteria2;
import com.gnc.dto.FishDto;
import com.gnc.dto.YgDto;

@Mapper
public interface YgDao {
	public List<YgDto> ygList(@Param("year")String year);
	
	public List<String> ygSearchListDao(@Param("year")String year,@Param("pageStart")int start,@Param("perPageNum")int perPageNum);
	
	public int getCount ();
	
	public Integer avgDao();
	
	public int getSearchCount (@Param("year")String year);
	
	public Integer avgSearchDao(@Param("year")String year);
	
	public List<String> ygSearchListDao2(@Param("year")String year
			,@Param("pageStart")int start,@Param("perPageNum")int perPageNum
			,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address,@Param("order")String order);
	
	public int getSearchCount2(@Param("year")String year,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address);
	
	public Integer avgSearchDao2(@Param("year")String year,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address);
}
