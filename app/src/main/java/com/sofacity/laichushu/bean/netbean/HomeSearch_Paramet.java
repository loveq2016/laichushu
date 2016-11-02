package com.sofacity.laichushu.bean.netbean;

/**
 * home 搜索
 * Created by wangtong on 2016/11/2.
 */
public class HomeSearch_Paramet {
    private String complexName;
    private String pageSize;
    private String pageNo;

    public HomeSearch_Paramet(String name, String pageSize, String pageNo) {
        this.complexName = name;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }
}