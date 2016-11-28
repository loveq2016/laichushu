package com.laichushu.book.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.JsonBean.RewardResult;
import com.laichushu.book.mvp.commentdetail.CommentDetailModle;
import com.laichushu.book.mvp.mechanismtopiclist.MechanismTopicListModel;
import com.laichushu.book.mvp.topicdetail.TopicDetailPresenter;
import com.laichushu.book.mvp.topicdetail.TopicDetailView;
import com.laichushu.book.mvp.topicdetail.TopicdetailModel;
import com.laichushu.book.ui.adapter.CommentDetaileAdapter;
import com.laichushu.book.ui.base.MvpActivity2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;

/**
 * 话题详情
 * Created by wangtong on 2016/11/26.
 */

public class TopicDetilActivity extends MvpActivity2<TopicDetailPresenter> implements TopicDetailView, View.OnClickListener, PullLoadMoreRecyclerView.PullLoadMoreListener {

    private MechanismTopicListModel.DataBean bean;
    private ImageView topicUserheadIv;
    private TextView topicAuthorTv;
    private TextView topicContentTv;
    private TextView topicTiemTv;
    private TextView topicNameTv;
    private TextView titleTv;
    private ImageView finishIv;
    private PullLoadMoreRecyclerView commentRyv;
    private int pageNo = 1;
    private CommentDetaileAdapter mAdapter;
    private ArrayList<CommentDetailModle.DataBean> mData = new ArrayList<>();
    private String topicId;
    private EditText sendmsgEv;
    private ImageView sendmsgIv;

    @Override
    protected TopicDetailPresenter createPresenter() {
        return new TopicDetailPresenter(this);
    }

    @Override
    protected View createSuccessView() {
        View mSuccessView = UIUtil.inflate(R.layout.activity_topicdetail);
        titleTv = (TextView) mSuccessView.findViewById(R.id.tv_title);
        finishIv = (ImageView) mSuccessView.findViewById(R.id.iv_title_finish);
        topicUserheadIv = (ImageView) mSuccessView.findViewById(R.id.iv_topic_userhead);
        topicAuthorTv = (TextView) mSuccessView.findViewById(R.id.tv_topic_author);
        topicContentTv = (TextView) mSuccessView.findViewById(R.id.tv_topic_content);
        topicTiemTv = (TextView) mSuccessView.findViewById(R.id.tv_topic_time);
        topicNameTv = (TextView) mSuccessView.findViewById(R.id.tv_topic_name);
        commentRyv = (PullLoadMoreRecyclerView)mSuccessView.findViewById(R.id.ryv_comment);
        sendmsgEv = (EditText)mSuccessView.findViewById(R.id.et_sendmsg);
        sendmsgIv = (ImageView)mSuccessView.findViewById(R.id.iv_sendmsg);
        commentRyv.setLinearLayout();
        commentRyv.setOnPullLoadMoreListener(this);
        commentRyv.setFooterViewText("加载中");
        mAdapter = new CommentDetaileAdapter(this,mData);
        commentRyv.setAdapter(mAdapter);
        finishIv.setOnClickListener(this);
        sendmsgIv.setOnClickListener(this);
        return mSuccessView;
    }

    @Override
    protected void initData() {
        titleTv.setText("话题详情");
        bean = getIntent().getParcelableExtra("bean");
        topicId = bean.getId();
        mvpPresenter.loadCommentData(topicId);
    }

    @Override
    public void getDataSuccess(TopicdetailModel model) {

        commentRyv.setPullLoadMoreCompleted();
        if (model.isSuccess()) {
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            if (model.getData().size() != 0) {
                mData.addAll(model.getData());
                mAdapter.setmData(mData);
                mAdapter.notifyDataSetChanged();
                pageNo++;
                topicNameTv.setText(bean.getTitle());
                topicAuthorTv.setText(bean.getCreatUserName());
                topicContentTv.setText(bean.getContent());
                topicTiemTv.setText(bean.getCreateDate());
                GlideUitl.loadRandImg(mActivity,bean.getCreaterPhoto(),topicUserheadIv);
            }
        } else {
            refreshPage(LoadingPager.PageState.STATE_ERROR);
            reLoadDate();
        }
    }

    @Override
    public void SaveScoreLikeData(RewardResult model, String type) {

    }

    @Override
    public void getSendDataSuccess(RewardResult model) {
        if (model.isSuccess()) {
            ToastUtil.showToast("发送成功");
            sendmsgEv.setText("");
            onRefresh();
        }else {
            ToastUtil.showToast("发送失败");
        }
    }

    @Override
    public void getDataFail(String msg) {
        refreshPage(LoadingPager.PageState.STATE_ERROR);
        reLoadDate();
    }

    @Override
    public void getDataFail2(String msg) {
        hideLoading();
        ToastUtil.showToast("发送失败");
    }

    @Override
    public void showLoading() {
        showProgressDialog();
    }

    @Override
    public void hideLoading() {
        dismissProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_title_finish:
                finish();
                break;
            case R.id.iv_sendmsg:
                mvpPresenter.topicDetailCommentSave(sendmsgEv, topicId);
                break;
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        pageNo = 1;
        mData.clear();
        mvpPresenter.getParamet().setPageNo(pageNo+"");
        mvpPresenter.loadCommentData(topicId);
    }

    /**
     * 上拉刷新
     */
    @Override
    public void onLoadMore() {
        mvpPresenter.getParamet().setPageNo(pageNo+"");
        mvpPresenter.loadCommentData(topicId);
    }
    public void reLoadDate(){
        mPage.setmListener(new LoadingPager.ReLoadDataListenListener() {
            @Override
            public void reLoadData() {
                refreshPage(LoadingPager.PageState.STATE_LOADING);
                mvpPresenter.loadCommentData(topicId);
            }
        });
    }
}
