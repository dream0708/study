package com.sohu.bp.elite.action.bean;

import com.sohu.bp.elite.constants.Constants;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
public class PageBean {
    private List<Integer> pageCapacities = Arrays.asList(10, 20, 50);
    private int currPageNo = 1;
    private int pageSize = 12;
    private long total = 0;

    private int startPageNo = 1;
    private int prePageNo = -1;
    private int nextPageNo = -1;
    private int endPageNo = 1;
    private int totalPage = 0;

    public PageBean(){

    }

    public void initPageBean(){
        endPageNo = (int) (total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        totalPage = endPageNo;

        if(currPageNo < 1 || currPageNo > endPageNo)
            currPageNo = 1;

        prePageNo = currPageNo - 1;
        if(prePageNo < 1){
            prePageNo = -1;
        }

        nextPageNo = currPageNo + 1;
        if(nextPageNo > endPageNo){
            nextPageNo = -1;
        }
    }

    public void setTotal(long total) {
        this.total = total;
        initPageBean();
    }

    public int getCurrPageNo() {
        if(currPageNo < 1){
            currPageNo = 1;
        }
        return currPageNo;
    }

    public int getPageSize() {
        if(pageSize < 1){
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    public List<Integer> getPageCapacities() {
        return pageCapacities;
    }

    public void setPageCapacities(List<Integer> pageCapacities) {
        this.pageCapacities = pageCapacities;
    }

    public void setCurrPageNo(int currPageNo) {
        this.currPageNo = currPageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public int getStartPageNo() {
        return startPageNo;
    }

    public void setStartPageNo(int startPageNo) {
        this.startPageNo = startPageNo;
    }

    public int getPrePageNo() {
        return prePageNo;
    }

    public void setPrePageNo(int prePageNo) {
        this.prePageNo = prePageNo;
    }

    public int getNextPageNo() {
        return nextPageNo;
    }

    public void setNextPageNo(int nextPageNo) {
        this.nextPageNo = nextPageNo;
    }

    public int getEndPageNo() {
        return endPageNo;
    }

    public void setEndPageNo(int endPageNo) {
        this.endPageNo = endPageNo;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
