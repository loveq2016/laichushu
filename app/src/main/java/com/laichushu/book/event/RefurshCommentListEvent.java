package com.laichushu.book.event;

/**
 * 刷新活动列表
 * Created by wangtong on 2016/11/9.
 */

public class RefurshCommentListEvent {
    public boolean isRefursh;
    public String type;
    public int size;
    public RefurshCommentListEvent(boolean isRefursh, int size) {
        this.isRefursh = isRefursh;
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
