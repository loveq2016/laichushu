package com.laichushu.book.bean.netbean;

import java.io.Serializable;

/**
 * Created by PCPC on 2016/11/23.
 */

public class HomeUserFocusBe_parmet implements Serializable {
    private String loginUserId, pageNo, pageSize;

    public HomeUserFocusBe_parmet(String loginUserId, String pageNo, String pageSize) {
        this.loginUserId = loginUserId;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
}
