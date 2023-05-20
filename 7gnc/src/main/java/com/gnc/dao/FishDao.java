package com.gnc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.Criteria2;
import com.gnc.dto.FishDto;

@Mapper
public interface FishDao {
	public List<FishDto> fishList(@Param("year")String year);
	
	public List<String> fishListDao(Criteria2 cri);
	
	public List<String> fishSearchListDao(@Param("year")String year,@Param("pageStart")int start,@Param("perPageNum")int perPageNum);
	
	public List<String> fishSearchListDao2(@Param("year")String year
			,@Param("pageStart")int start,@Param("perPageNum")int perPageNum
			,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address,@Param("order")String order);
	
	public int getCount ();
	
	public Integer avgDao();
	
	public int getSearchCount (@Param("year")String year);
	
	public int getSearchCount2(@Param("year")String year,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address);
	
	public Integer avgSearchDao(@Param("year")String year);
	
	public Integer avgSearchDao2(@Param("year")String year,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address);
}
