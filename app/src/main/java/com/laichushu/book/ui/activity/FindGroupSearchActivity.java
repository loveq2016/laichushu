package com.laichushu.book.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.db.Search_History;
import com.laichushu.book.db.Search_HistoryDao;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.mvp.find.group.GroupListModle;
import com.laichushu.book.mvp.find.group.groupsearch.FindGroupModle;
import com.laichushu.book.mvp.find.group.groupsearch.FindGroupSearchPresenter;
import com.laichushu.book.mvp.find.group.groupsearch.FindGroupSearchView;
import com.laichushu.book.ui.adapter.GroupSearchHistoryAdapter;
import com.laichushu.book.ui.adapter.GroupSearchListAdapter;
import com.laichushu.book.ui.base.MvpActivity2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 发现 - 小组 - 搜索
 * Created by wangtong on 2016/12/27.
 */

public class FindGroupSearchActivity extends MvpActivity2<FindGroupSearchPresenter> implements FindGroupSearchView, View.OnClickListener, TextView.OnEditorActionListener, PullLoadMoreRecyclerView.PullLoadMoreListener, AdapterView.OnItemClickListener {

    private PullLoadMoreRecyclerView mRecyclerView;
    private ImageView finishIV;
    private EditText searchEt;
    private String search;
    private TextView clearTv;
    private LinearLayout searchLay;
    private ListView searchLv;
    private ImageView emptyIv;
    private ArrayList<GroupListModle.DataBean> mData = new ArrayList<>();
    private int pageNo = 1;
    private GroupSearchListAdapter mAdapter;
    private Search_HistoryDao dao;
    private List<Search_History> hisList = new ArrayList<>();
    private GroupSearchHistoryAdapter historyAdapter;
    private String type = ConstantValue.SEARCH_TYPE_GROUP;
    /**
     * @return P 控制器
     */
    @Override
    protected FindGroupSearchPresenter createPresenter() {
        return new FindGroupSearchPresenter(this);
    }

    /**
     * @return 成功页面
     */
    @Override
    protected View createSuccessView() {
        View mSuccessView = UIUtil.inflate(R.layout.activity_findgroupsearch);
        finishIV = (ImageView) mSuccessView.findViewById(R.id.iv_finish);//返回
        searchEt = (EditText) mSuccessView.findViewById(R.id.et_search);//搜索
        clearTv = (TextView) mSuccessView.findViewById(R.id.tv_clear);//清除搜索历史
        searchLay = (LinearLayout) mSuccessView.findViewById(R.id.lay_search);//整个搜索布局
        searchLv = (ListView) mSuccessView.findViewById(R.id.lv_history);//搜索历史的容器
        mRecyclerView = (PullLoadMoreRecyclerView) mSuccessView.findViewById(R.id.ryv_search);//搜索列表
        emptyIv = (ImageView) mSuccessView.findViewById(R.id.iv_empty);//搜索空结果
        mRecyclerView.setLinearLayout();
        mAdapter = new GroupSearchListAdapter(this,mData);
        mRecyclerView.setAdapter(mAdapter);
        finishIV.setOnClickListener(this);
        searchEt.setOnClickListener(this);
        clearTv.setOnClickListener(this);
        searchEt.setOnEditorActionListener(this);
        mRecyclerView.setOnPullLoadMoreListener(this);
        searchLv.setOnItemClickListener(this);
        return mSuccessView;
    }

    /**
     * 数据初始化
     */
    @Override
    protected void initData() {
        super.initData();
        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshPage(LoadingPager.PageState.STATE_SUCCESS);//加载成功页面
            }
        },10);
        mvpPresenter.setupDatabase();
        dao = mvpPresenter.getSearch_historyDao();
        List<Search_History> hisList = mvpPresenter.getHistoryList();
        if (hisList != null) {//将历史记录添加到集合中 并反转顺序
            this.hisList.addAll(hisList);//
            Collections.reverse(this.hisList);
            historyAdapter = new GroupSearchHistoryAdapter(this.hisList, this);
            searchLv.setAdapter(historyAdapter);
        }
    }

    /**
     * 点击事件
     * @param v 点击按钮
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_finish:
                finish();
                break;
            case R.id.et_search:
                searchLay.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                emptyIv.setVisibility(View.GONE);
                break;
            case R.id.tv_clear:
                if (hisList != null) {
                    for (int i = hisList.size() - 1; i >= 0; i--) {
                        dao.delete(hisList.get(i));
                    }
                    hisList.clear();
                    historyAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 软键盘监听
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //处理事件
            mData.clear();
            search = searchEt.getText().toString().trim();
            mvpPresenter.loadSearchResultData(search);
            boolean isSearch = true;
            if (hisList.size() != 0) {
                for (int i = 0; i < hisList.size(); i++) {
                    if (hisList.get(i).getHistory().equals(search)) {
                        isSearch = false;
                        Search_History history1 = hisList.get(i);
                        Search_History history2 = hisList.get((hisList.size() - 1));
                        hisList.set(i, history2);
                        hisList.set(hisList.size() - 1, history1);
                        mAdapter.notifyDataSetChanged();
                        dao.updateInTx(history1, history2);
                        break;
                    }
                }
            }
            if (isSearch) {//判断是否相同
                //将记录添加到数据库中
                Search_History history = new Search_History(null, type,search);
                if (hisList.size() != 0) {
                    for (int i = hisList.size(); i > 0; i--) {
                        if (i == hisList.size()) {
                            hisList.add(hisList.get(i - 1));
                        } else {
                            hisList.set(i, hisList.get(i - 1));
                        }
                        if (i == 1) {
                            hisList.remove(1);
                        }
                    }
                }
                hisList.add(0, history);
                dao.insert(history);

                if (hisList.size() > 5) {
                    dao.delete(hisList.get(5));
                    hisList.remove(5);
                }
                historyAdapter.setList(hisList);
                historyAdapter.notifyDataSetChanged();
            }
        }
        return false;
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mData.clear();
        pageNo = 1;
        mvpPresenter.getParamet().setPageNo(pageNo+"");
        mvpPresenter.loadSearchResultData(search);//请求网络获取搜索列表
    }

    /**
     * 上拉刷新
     */
    @Override
    public void onLoadMore() {
        mvpPresenter.getParamet().setPageNo(pageNo+"");
        mvpPresenter.loadSearchResultData(search);//请求网络获取搜索列表
    }

    /**
     * ListView条目点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.lv_history://历史记录
                searchEt.setText(hisList.get(position).getHistory());
                onEditorAction(searchEt, EditorInfo.IME_ACTION_SEARCH, null);
                break;
        }
    }

    /**
     * 搜索小组接口返回数据 成功
     * @param model
     */
    @Override
    public void searchGroupDataSuccess(FindGroupModle model) {
        UIUtil.postPullLoadMoreCompleted(mRecyclerView);
        if (model.isSuccess()) {
            if (!model.getData().getTeamList().isEmpty()) {
                searchLay.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mData.addAll(model.getData().getTeamList());
                mAdapter.setmData(mData);
                pageNo++;
            }else {
                if (mvpPresenter.getParamet().getPageNo().equals("1")){
                    emptyIv.setVisibility(View.VISIBLE);
                    searchLay.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                }else {
                    ToastUtil.showToast("没有更多数据了");
                }
            }
        } else {
            ToastUtil.showToast(model.getErrMsg());
        }
    }

    /**
     * 搜索小组接口返回数据 失败
     * @param msg
     */
    @Override
    public void searchGroupDataFail(String msg) {
        ToastUtil.showToast("搜索失败");
    }

    /**
     * @return 控制器2
     */
    public FindGroupSearchPresenter getPresenter() {
        return mvpPresenter;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {//创建小组后
            Bundle bundle = data.getExtras();
            String str = bundle.getString("back");
            if (str.equals("updata")) {//刷新小组列表
                onRefresh();
            }
        }else if (resultCode == 4){//更新小组人数
            Bundle bundle = data.getExtras();
            int argsMember = bundle.getInt("argsMember");
            int index = bundle.getInt("index");
            mData.get(index).setJoinNum(mData.get(index).getJoinNum()+argsMember);
            mAdapter.setmData(mData);
        }
    }
}
