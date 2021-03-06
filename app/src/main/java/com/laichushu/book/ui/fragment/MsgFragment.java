package com.laichushu.book.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.JsonBean.RewardResult;
import com.laichushu.book.bean.netbean.BookDetailsModle;
import com.laichushu.book.bean.netbean.MessageCommentResult;
import com.laichushu.book.bean.netbean.PerMsgInfoReward;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.mvp.home.homelist.HomeHotModel;
import com.laichushu.book.mvp.msg.messagecomment.MessageCommentPresenter;
import com.laichushu.book.mvp.msg.messagecomment.MessageCommentView;
import com.laichushu.book.ui.activity.BookDetailActivity;
import com.laichushu.book.ui.activity.MessageCommentDetailsActivity;
import com.laichushu.book.ui.activity.MsgLikeDetailsActivity;
import com.laichushu.book.ui.adapter.SubMissionAdapter;
import com.laichushu.book.ui.base.MvpFragment2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.ModelUtils;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;
import com.orhanobut.logger.Logger;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息
 * Created by wangtong on 2016/10/17.
 */
public class MsgFragment extends MvpFragment2<MessageCommentPresenter> implements MessageCommentView, View.OnClickListener, RadioGroup.OnCheckedChangeListener, PullLoadMoreRecyclerView.PullLoadMoreListener {
    private ImageView ivFinish;
    private TextView tvTitle;
    private RelativeLayout rlComment, rlLike, rlReward, rlFocus, rlLetter, rlScribe, rlActivityMsg, rlOtherMsg;
    private PullLoadMoreRecyclerView mRecyclerView;
    private SubMissionAdapter subAdapter;
    private List<MessageCommentResult.DataBean> subData = new ArrayList<>();
    private RadioGroup radioGroup;
    private LinearLayout llContainer, llSys;
    private int PAGE_NO = 1;
    private boolean dibbleSub;

    @Override
    protected MessageCommentPresenter createPresenter() {
        return new MessageCommentPresenter(this);
    }

    @Override
    public View createSuccessView() {
        View mRootView = UIUtil.inflate(R.layout.fragment_msg);
        ivFinish = (ImageView) mRootView.findViewById(R.id.iv_title_finish);
        tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        rlComment = (RelativeLayout) mRootView.findViewById(R.id.rl_comment);
        mRecyclerView = (PullLoadMoreRecyclerView) mRootView.findViewById(R.id.ryv_subMission);
        radioGroup = (RadioGroup) mRootView.findViewById(R.id.rg_msgList);
        llContainer = (LinearLayout) mRootView.findViewById(R.id.ll_container);
        llSys = (LinearLayout) mRootView.findViewById(R.id.ll_sysMsg);
        rlLike = (RelativeLayout) mRootView.findViewById(R.id.rl_msgLike);
        rlReward = (RelativeLayout) mRootView.findViewById(R.id.rl_reward);
        rlFocus = (RelativeLayout) mRootView.findViewById(R.id.rl_msgFocus);
        rlLetter = (RelativeLayout) mRootView.findViewById(R.id.rl_priLetter);
        rlScribe = (RelativeLayout) mRootView.findViewById(R.id.rl_subScribe);
        rlActivityMsg = (RelativeLayout) mRootView.findViewById(R.id.rl_activityMsg);
        rlOtherMsg = (RelativeLayout) mRootView.findViewById(R.id.rl_otherMsg);
        return mRootView;
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("消息");
        ivFinish.setVisibility(View.INVISIBLE);

        rlComment.setOnClickListener(this);
        llSys.setOnClickListener(this);
        rlLike.setOnClickListener(this);
        rlReward.setOnClickListener(this);
        rlFocus.setOnClickListener(this);
        rlLetter.setOnClickListener(this);
        rlScribe.setOnClickListener(this);
        rlActivityMsg.setOnClickListener(this);
        rlOtherMsg.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        //初始化mRecyclerView 投稿
        mRecyclerView.setGridLayout(1);
        subAdapter = new SubMissionAdapter(getActivity(), subData, mvpPresenter);
        mRecyclerView.setAdapter(subAdapter);
        mRecyclerView.setOnPullLoadMoreListener(this);

        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            }
        }, 10);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comment:
                //评论
                UIUtil.openActivity(mActivity, MessageCommentDetailsActivity.class);
                break;
            case R.id.rl_msgLike:
                //喜欢
                Bundle like = new Bundle();
                like.putString("type", "1");
                UIUtil.openActivity(mActivity, MsgLikeDetailsActivity.class, like);
                break;
            case R.id.rl_reward:
                //打赏
                Bundle reward = new Bundle();
                reward.putString("type", "2");
                UIUtil.openActivity(mActivity, MsgLikeDetailsActivity.class, reward);
                break;
            case R.id.rl_msgFocus:
                //关注
                Bundle bundle = new Bundle();
                bundle.putString("type", "3");
                UIUtil.openActivity(mActivity, MsgLikeDetailsActivity.class, bundle);
                break;
            case R.id.rl_priLetter:
                //私信
                Bundle letter = new Bundle();
                letter.putString("type", "4");
                UIUtil.openActivity(mActivity, MsgLikeDetailsActivity.class, letter);
                break;
            case R.id.rl_subScribe:
                //订阅
                Bundle scribe = new Bundle();
                scribe.putString("type", "5");
                UIUtil.openActivity(mActivity, MsgLikeDetailsActivity.class, scribe);
                break;
            case R.id.rl_activityMsg:
                //活动通知
                Bundle activity = new Bundle();
                activity.putString("type", "6");
                UIUtil.openActivity(mActivity, MsgLikeDetailsActivity.class, activity);
                break;
            case R.id.rl_otherMsg:
                //其他消息
                Bundle other = new Bundle();
                other.putString("type", "7");
                UIUtil.openActivity(mActivity, MsgLikeDetailsActivity.class, other);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_perMsg:
                //首页
                dibbleSub = false;
                llSys.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                llContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_submissionMsg:
                //投稿
                llContainer.setVisibility(View.GONE);
                llSys.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mvpPresenter.getParamet().setMsgType(ConstantValue.MSG_TYPE_2);
                if (!dibbleSub) {
                    showProgressDialog();
                    mvpPresenter.LoaCommentdData();
                }
                dibbleSub = true;
                break;
            case R.id.rb_sysMsg:
                //系统消息
                dibbleSub = false;
                llContainer.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                llSys.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public void onRefresh() {
        PAGE_NO = 1;
        subData.clear();
        mvpPresenter.getParamet().setPageNo(PAGE_NO + "");
        mvpPresenter.LoaCommentdData();//请求网络获取搜索列表

    }

    @Override
    public void onLoadMore() {
        mvpPresenter.getParamet().setPageNo(PAGE_NO + "");
        mvpPresenter.LoaCommentdData();//请求网络获取搜索列表
    }

    @Override
    public void getMsgCommentDateSuccess(MessageCommentResult model) {
        dismissProgressDialog();
        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPullLoadMoreCompleted();
            }
        }, 300);
        if (model.isSuccess()) {
            if (null != model.getData() && !model.getData().isEmpty()) {
                subData.clear();
                subData = model.getData();
                PAGE_NO++;
            } else {
                ToastUtil.showToast(R.string.errMsg_empty);
            }
            subAdapter.refreshAdapter(subData);
        } else {
            ToastUtil.showToast(model.getErrMSg());
        }
        refreshPage(LoadingPager.PageState.STATE_SUCCESS);
    }

    @Override
    public void messageDeleteCommentSuccess(RewardResult model, int position) {
        if (model.isSuccess()) {
            ToastUtil.showToast("删除成功");
            subData.remove(position);
            subAdapter.refreshAdapter(subData);
        } else {
            ToastUtil.showToast("删除失败");
        }
    }

    @Override
    public void getBookDetailsDateSuccess(HomeHotModel model, int position) {

    }

    @Override
    public void sendMsgDetailsDateSuccess(RewardResult model) {

    }

    @Override
    public void getPerInfoListDateSuccess(MessageCommentResult model) {

    }

    @Override
    public void getPerInfoDetailsDateSuccess(PerMsgInfoReward model) {

    }

    @Override
    public void getSendDataSuccess(RewardResult model) {

    }

    @Override
    public void getDelPerIdfoDataSuccess(RewardResult model) {

    }

    @Override
    public void getBookDetailsByIdDataSuccess(BookDetailsModle model) {
        if (model.isSuccess()) {
            //跳转图书详情页
            Bundle bundle = new Bundle();
//        String bd = gson.toJson(model, BookDetailsModle.class);
//        HomeHotModel.DataBean homeHotModel = gson.fromJson(bd, new TypeToken<HomeHotModel.DataBean>() {}.getType());
            HomeHotModel.DataBean dataBean = ModelUtils.bean2HotBean(model);
            bundle.putParcelable("bean", dataBean);
            bundle.putString("pageMsg", "消息喜欢");
            UIUtil.openActivity(mActivity, BookDetailActivity.class, bundle);
        } else {
            ToastUtil.showToast(model.getErrMsg());
        }
    }

    @Override
    public void getDataFail(String msg, int flg) {
        dismissDialog();
        ToastUtil.showToast(R.string.errMsg_data_exception);
        Logger.e(msg);
    }

    @Override
    public void showDialog() {
        showProgressDialog();
    }

    @Override
    public void dismissDialog() {
        dismissProgressDialog();
    }


}
