package com.sofacity.laichushu.mvp.home;

import com.sofacity.laichushu.bean.netbean.HomeAllBook_Paramet;
import com.sofacity.laichushu.retrofit.ApiCallback;
import com.sofacity.laichushu.ui.base.BasePresenter;

/**
 * 首页 presenter
 * Created by wangtong on 2016/10/17.
 */
public class HomePresenter extends BasePresenter<HomeView> {
    private String pageSize = "10";
    private String pageNo = "1";
    private HomeAllBook_Paramet paramet = paramet = new HomeAllBook_Paramet("1",pageSize,pageNo);

    public HomePresenter(HomeView view) {
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
        addSubscription(apiStores.homeHotData(), new ApiCallback<HomeHotModel>() {
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
        mvpView.showLoading();
        getParamet().setSortWay(type);
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
