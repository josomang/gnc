package com.gnc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.ArfishDto;
import com.gnc.dto.Criteria2;

@Mapper
public interface ArfishDao {
	public List<ArfishDto> arfishList(@Param("year")String year);
	
	public List<String> arfishListDao(Criteria2 cri);
	
	public List<String> arfishSearchListDao(@Param("year")String year,@Param("pageStart")int start,@Param("perPageNum")int perPageNum);
	
	
	public int getSearchCount (@Param("year")String year);
	
	public Integer avgSearchDao(@Param("year")String year);
	
	public List<String> arfishSearchListDao2(@Param("year")String year
			,@Param("pageStart")int start,@Param("perPageNum")int perPageNum
			,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address,@Param("order")String order);
	
	public int getSearchCount2(@Param("year")String year,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address);
	
	public Integer avgSearchDao2(@Param("year")String year,@Param("age")String age,@Param("gender")String gender
			,@Param("address")String address);
}
