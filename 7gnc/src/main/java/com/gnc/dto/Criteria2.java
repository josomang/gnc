package com.gnc.dto;

import lombok.Data;

@Data
public class Criteria2 {
    
    private int page;
    private int perPageNum; 
    
    private Paging paging;
    
    
    
    public int getPageStart() {
       
        return (this.page -1) * perPageNum;
    }
 
    public Criteria2() {
      
        this.page = 1;
        this.perPageNum = 10;
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