package com.laichushu.book.mvp.init;

import com.laichushu.book.bean.netbean.HomeAllBook_Paramet;
import com.laichushu.book.bean.netbean.HomeHot_Paramet;
import com.laichushu.book.mvp.home.HomeHotModel;
import com.laichushu.book.mvp.home.HomeModel;
import com.laichushu.book.retrofit.ApiCallback;
import com.laichushu.book.ui.base.BasePresenter;
import com.laichushu.book.utils.SharePrefManager;

/**
 * init presenter
 * Created by wangtong on 2016/10/17.
 */
public class InitPresenter extends BasePresenter<InitView> {
    private String pageSize = "10";
    private String pageNo = "1";
    private HomeAllBook_Paramet paramet;
    private String userId = SharePrefManager.getUserId();

    public InitPresenter(InitView view) {
        attachView(view);
    }
    public void loadHomeCarouseData() {
        mvpView.showLoading();
        addSubscription(apiStores.homeCarouselData(),
                new ApiCallback<HomeModel>() {
                    @Override
                    public void onSuccess(HomeModel model) {
                        mvpView.getDataSuccess(model);
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

    public void loadHomeHotData() {
        mvpView.showLoading();
        addSubscription(apiStores.homeHotData(new HomeHot_Paramet(userId)), new ApiCallback<HomeHotModel>() {
            @Override
            public void onSuccess(HomeHotModel model) {
                mvpView.getHotDataSuccess(model);
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
    public void loadHomeAllData(String type) {
        paramet = new HomeAllBook_Paramet(type,pageSize,pageNo,userId);
        mvpView.showLoading();
        addSubscription(apiStores.homeAllData(paramet), new ApiCallback<HomeHotModel>() {
            @Override
            public void onSuccess(HomeHotModel model) {
                mvpView.getAllData(model);
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

    public HomeAllBook_Paramet getParamet() {
        return paramet;
    }

    public void setParamet(HomeAllBook_Paramet paramet) {
        this.paramet = paramet;
    }
}