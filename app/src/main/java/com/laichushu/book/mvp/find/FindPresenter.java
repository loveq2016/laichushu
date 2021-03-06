package com.laichushu.book.mvp.find;

import com.laichushu.book.bean.netbean.CourseCommendation_Paramet;
import com.laichushu.book.bean.netbean.FindCourseCommResult;
import com.laichushu.book.bean.netbean.FindLessonListResult;
import com.laichushu.book.bean.netbean.FindLessonList_Paramet;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.mvp.find.coursera.video.CourseraModle;
import com.laichushu.book.retrofit.ApiCallback;
import com.laichushu.book.ui.base.BasePresenter;
import com.laichushu.book.ui.fragment.FindFragment;

/**
 * 发现 - fragment - Presenter
 * Created by PCPC on 2016/12/19.
 */

public class FindPresenter extends BasePresenter<FindView> {
    private String pageSize = ConstantValue.PAGESIZE;
    private String pageNo = "1";
    private String userId = ConstantValue.USERID;
    private FindFragment findFragment;
    private String pageSize1=ConstantValue.PAGESIZE5;

    public FindPresenter(FindView view) {
        attachView(view);
        findFragment = (FindFragment) view;
    }

    //精选课程
    FindLessonList_Paramet paramet = new FindLessonList_Paramet("1","","",pageNo,pageSize1);

    public void loadFindLessonListCommData() {
        addSubscription(apiStores.findLessonListDatails(paramet),
                new ApiCallback<CourseraModle>() {
                    @Override
                    public void onSuccess(CourseraModle model) {
                        mvpView.getLessonListDataSuccess(model);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        mvpView.getDataFail("code+" + code + "/msg:" + msg);
                    }

                    @Override
                    public void onFinish() {
                        mvpView.hideLoading();
                    }
                });
    }

    //小组推荐
    public void loadFindCourseCommData() {
        addSubscription(apiStores.findCourseCommendationDatails(new CourseCommendation_Paramet(userId)),
                new ApiCallback<FindCourseCommResult>() {
                    @Override
                    public void onSuccess(FindCourseCommResult model) {
                        mvpView.getCourseDataSuccess(model);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        mvpView.getDataFail("code+" + code + "/msg:" + msg);
                    }

                    @Override
                    public void onFinish() {
                        mvpView.hideLoading();
                    }
                });
    }
}
