package com.gnc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gnc.dto.Criteria2;

@Mapper
public interface ArfishDao {
	public List<String> arfishListDao(Criteria2 cri);
	
	public List<String> arfishSearchListDao(@Param("year")String year,@Param("pageStart")int start,@Param("perPageNum")int perPageNum);
	
	
	public int getSearchCount (@Param("year")String year);
	
	public Integer avgSearchDao(@Param("year")String year);
}
