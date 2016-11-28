package com.laichushu.book.mvp.userhomepage;

import com.laichushu.book.bean.netbean.HomePersonFocusResult;
import com.laichushu.book.bean.netbean.HomeUseDyrResult;
import com.laichushu.book.bean.netbean.HomeUserResult;
import com.laichushu.book.mvp.home.HomeHotModel;

/**
 * Created by PCPC on 2016/11/25.
 */

public interface UserHomePageView {
    void getUserHeadDateSuccess(HomeUserResult result);
    void getUserHomeDyDateSuccess(HomeUseDyrResult result);
    void getUserHomeBookListSuccess(HomeHotModel result);
    void getUserHomeFocusHeSuccess(HomePersonFocusResult result);
    void getUserHomeHeFocusSuccess(HomePersonFocusResult result);
    void getDataFail(String errorMsg);
}