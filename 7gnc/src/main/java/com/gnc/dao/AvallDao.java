package com.gnc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.AvallDto;
import com.gnc.dto.Criteria2;

@Mapper
public interface AvallDao {
	public List<AvallDto> avallList(@Param("year")String year);
	
	public List<String> avallListDao(Criteria2 cri);
	
	public List<String> avallSearchListDao(@Param("year")String year,@Param("pageStart")int start,
			@Param("perPageNum")int perPageNum,@Param("order")String order);
	
	public int getCount ();
	
	public Integer avgDao();
	
	public int getSearchCount (@Param("year")String year);
	
	public Integer avgSearchDao(@Param("year")String year);
	
	public Integer avgSearchDao2(@Param("year")String year);
}
