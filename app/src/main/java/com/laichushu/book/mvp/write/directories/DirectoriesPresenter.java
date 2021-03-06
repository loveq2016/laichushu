package com.laichushu.book.mvp.write.directories;

import com.google.gson.Gson;
import com.laichushu.book.bean.netbean.BookList_Paramet;
import com.laichushu.book.bean.netbean.MaterialContent_Paramet;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.ui.base.BasePresenter;
import com.orhanobut.logger.Logger;
import com.laichushu.book.bean.netbean.MaterialList_Paramet;
import com.laichushu.book.retrofit.ApiCallback;
import com.laichushu.book.ui.activity.DirectoriesActivity;

/**
 * 目录 presenter
 * Created by wangtong on 2016/10/12.
 */
public class DirectoriesPresenter extends BasePresenter<DirectoriesView> {
    private DirectoriesActivity mActivity;
    private String pageNo = "1";
    private String pageSize = ConstantValue.PAGESIZE;
    private String userId = ConstantValue.USERID;
    BookList_Paramet patamet = new BookList_Paramet("",userId,pageNo,pageSize);
    //初始化构造
    public DirectoriesPresenter(DirectoriesView view) {
        attachView(view);
        mActivity = (DirectoriesActivity) view;
    }
    public void loadMaterialListData(String articleId){
        mvpView.showLoading();
        MaterialList_Paramet paramet = new MaterialList_Paramet(articleId);
        Logger.e("获取素材列表");
        Logger.json(new Gson().toJson(paramet));
        addSubscription(apiStores.getMaterialList(paramet), new ApiCallback<MaterialListModel>() {
            @Override
            public void onSuccess(MaterialListModel model) {
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

    public void loadMaterialContentData(String parentId){
        mvpView.showLoading();
        MaterialContent_Paramet patamet = new MaterialContent_Paramet(parentId);
        Logger.e("获取素材列表");
        Logger.json(new Gson().toJson(patamet));
        addSubscription(apiStores.getMaterialContent(patamet), new ApiCallback<MaterialContentModel>() {
            @Override
            public void onSuccess(MaterialContentModel model) {
                mvpView.getMaterialContentData(model);
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

    public void loadBookData(String articleId){
        mvpView.showLoading();
        patamet.setArticleId(articleId);
        Logger.e("获取章节列表");
        Logger.json(new Gson().toJson(patamet));
        addSubscription(apiStores.getBookList(patamet), new ApiCallback<BookMoudle>() {
            @Override
            public void onSuccess(BookMoudle model) {
                mvpView.getBookListData(model);
            }

            @Override
            public void onFailure(int code, String msg) {
                mvpView.getDataFail2("code+" + code + "/msg:" + msg);
            }

            @Override
            public void onFinish() {
                mvpView.hideLoading();
            }
        });
    }

    public BookList_Paramet getPatamet() {
        return patamet;
    }

    public void setPatamet(BookList_Paramet patamet) {
        this.patamet = patamet;
    }
}
