package com.gnc.dto;

import lombok.Data;

@Data
public class Criteria {
    
    private int page;
    private int perPageNum; 
    
    private Paging paging;
    
    
    
    public int getPageStart() {
       
        return (this.page -1) * perPageNum;
    }
 
    public Criteria() {
      
        this.page = 1;
        this.perPageNum = 20;
    }
 

    public int getPage() {
        return page;
    }
 
    public void setPage(int page) {
        if(page <= 0) {
            this.page = 1;
            
        } else {
            this.page = page;
        }    
    }
 
    
    public int getPerPageNum() {
        return perPageNum;
    }
 
    public void setPerPageNum(int perPageNum) {
        int cnt = this.perPageNum;
        
        if(perPageNum != cnt) {
          
        } else {
            this.perPageNum = perPageNum;
        }
        
    }
    
  
}