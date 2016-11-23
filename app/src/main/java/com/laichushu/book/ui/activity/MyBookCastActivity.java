package com.laichushu.book.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.netbean.MyHomeModel;
import com.laichushu.book.mvp.bookcast.BookCastModle;
import com.laichushu.book.mvp.bookcast.BookcastPresener;
import com.laichushu.book.mvp.bookcast.BookcastView;
import com.laichushu.book.mvp.home.HomeHotModel;
import com.laichushu.book.ui.adapter.BookCastCollAdapter;
import com.laichushu.book.ui.adapter.MyBookCastAdapter;
import com.laichushu.book.ui.base.MvpActivity2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;
import com.orhanobut.logger.Logger;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyBookCastActivity extends MvpActivity2<BookcastPresener> implements BookcastView, View.OnClickListener, RadioGroup.OnCheckedChangeListener, PullLoadMoreRecyclerView.PullLoadMoreListener {
    private ImageView ivBack;
    private TextView tvTitle;
    private PullLoadMoreRecyclerView mRecyclerView,mCollRecyclerView;
    private RadioGroup radioGroup;
    private RadioButton rbScan, rbCollection;
    private View lineLeft, lineRight;
    private int PAGE_NO = 1;
    private List<HomeHotModel.DataBean> scanData = new ArrayList<>();
    private List<HomeHotModel.DataBean> collData = new ArrayList<>();
    private MyBookCastAdapter scanAdapter;
    private BookCastCollAdapter collAdapter;
    /**
     * 当前是否连续点击
     */
    private boolean scanDibble = false, collDibble = false;
    /**
     * type 1: 浏览  2：收藏
     */
    private int type = 1;

    @Override
    protected BookcastPresener createPresenter() {
        return new BookcastPresener(this);
    }

    @Override
    protected View createSuccessView() {
        View bookCastView = UIUtil.inflate(R.layout.activity_my_book_cast);
        ivBack = ((ImageView) bookCastView.findViewById(R.id.iv_title_finish));
        tvTitle = ((TextView) bookCastView.findViewById(R.id.tv_title));
        lineLeft = bookCastView.findViewById(R.id.myCast_left);
        lineRight = bookCastView.findViewById(R.id.myCast_right);
        mRecyclerView = (PullLoadMoreRecyclerView) bookCastView.findViewById(R.id.ryv_bookCast);
        mCollRecyclerView = (PullLoadMoreRecyclerView) bookCastView.findViewById(R.id.ryv_bookCastColl);
        radioGroup = ((RadioGroup) bookCastView.findViewById(R.id.rg_bookList));
        rbScan = ((RadioButton) bookCastView.findViewById(R.id.rb_scan));
        rbCollection = ((RadioButton) bookCastView.findViewById(R.id.rb_collection));

        return bookCastView;
    }


    @Override
    protected void initData() {
        rbScan.setChecked(true);
        lineLeft.setVisibility(View.VISIBLE);
        lineRight.setVisibility(View.INVISIBLE);
        mvpPresenter.LoadData();
        tvTitle.setText("作品管理");
        ivBack.setOnClickListener(this);
        lineLeft.setOnClickListener(this);
        lineRight.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        //初始化mRecyclerView Scan
        mRecyclerView.setGridLayout(3);
        mRecyclerView.setFooterViewText("加载中");
        scanAdapter = new MyBookCastAdapter(this, scanData);
        mRecyclerView.setAdapter(scanAdapter);
        mRecyclerView.setOnPullLoadMoreListener(this);
        //初始化mRecyclerView Coll
        mCollRecyclerView.setGridLayout(3);
        mCollRecyclerView.setFooterViewText("加载中");
        collAdapter = new BookCastCollAdapter(this, collData);
        mCollRecyclerView.setAdapter(collAdapter);
        mCollRecyclerView.setOnPullLoadMoreListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_finish:
                this.finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        PAGE_NO = 1;
        switch (checkedId) {
            case R.id.rb_scan:
                //点击浏览
                mCollRecyclerView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                collDibble = false;
                type = 1;
                lineLeft.setVisibility(View.VISIBLE);
                lineRight.setVisibility(View.INVISIBLE);
                if (!scanDibble) {
                    scanData.clear();
                    mvpPresenter.LoadData();
                }
                scanDibble = true;
                break;
            case R.id.rb_collection:
                //点击收藏
                mCollRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                scanDibble = false;
                type = 2;
                lineLeft.setVisibility(View.INVISIBLE);
                lineRight.setVisibility(View.VISIBLE);
                if (!collDibble) {
                    collData.clear();
                    mvpPresenter.LoadCollectionData();
                }
                collDibble = true;
                break;
        }
    }

    @Override
    public void onRefresh() {
        PAGE_NO = 1;

        if (type == 1) {
            scanData.clear();
            mvpPresenter.getParamet().setPageNo(PAGE_NO + "");
            mvpPresenter.LoadData();//请求网络获取搜索列表
        } else if (type == 2) {
            collData.clear();
            mvpPresenter.getParamet().setPageNo(PAGE_NO + "");
            mvpPresenter.LoadCollectionData();//请求网络获取搜索列表
        }


    }

    @Override
    public void onLoadMore() {
        if(type==1){
            mvpPresenter.getParamet().setPageNo(PAGE_NO + "");
            mvpPresenter.LoadData();//请求网络获取搜索列表
             }else if(type==2){
            mvpPresenter.getParamet().setPageNo(PAGE_NO + "");
            mvpPresenter.LoadCollectionData();;//请求网络获取搜索列表
        }

    }

    @Override
    public void getDataSuccess(HomeHotModel model) {
        scanData.clear();
        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPullLoadMoreCompleted();
            }
        }, 300);
        if (model.isSuccess()) {
            scanData = model.getData();
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            if (!scanData.isEmpty()) {
                scanAdapter.refreshAdapter(scanData);
                PAGE_NO++;
            } else {

            }
        } else {
            refreshPage(LoadingPager.PageState.STATE_ERROR);
        }
    }

    @Override
    public void getCollectionDataSuccess(HomeHotModel model) {
        scanData.clear();
        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPullLoadMoreCompleted();
            }
        }, 300);
        if (model.isSuccess()) {
            collData = model.getData();
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            if (!collData.isEmpty()) {
                collAdapter.refreshAdapter(collData);
                PAGE_NO++;
            } else {

            }
        } else {
            refreshPage(LoadingPager.PageState.STATE_ERROR);
        }
    }

    @Override
    public void getDataFail(String msg) {
        Logger.e(msg);
    }
}
