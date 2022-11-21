package com.gnc.dto;

import lombok.Data;

@Data
public class PageDTO {
	int page;
	private int startPage;
	private int endPage;
	private int totalCount;
	private int rowPerPage;
	private int lastPage;
	private int start;
	private int end;
	
	public PageDTO() {
		this.page=1;
		this.rowPerPage = 20;
		
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		pageingData();
	}
	public void pageingData() {
		
		this.lastPage =  (int) Math.ceil((double)totalCount/(double)this.rowPerPage);
		this.endPage =  (int) Math.ceil((double)page/(double)this.rowPerPage) * this.rowPerPage;
		
		if(this.lastPage<this.endPage) {
			this.endPage=this.lastPage;
		}
		
		this.startPage = this.endPage-this.rowPerPage+1;
		
		if(this.startPage<1) {
			this.startPage=1;
		}
		this.start= (this.page-1)* this.rowPerPage;
		this.end=this.rowPerPage;
		
		
	}
	
}
