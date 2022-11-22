package com.gnc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LibraryDao {
	public Integer getLibraryPeopleDao(@Param("TRGT_YR")String TRGT_YR);
}
