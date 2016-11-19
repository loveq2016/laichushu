package com.laichushu.book.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.netbean.PersonalCentreResult;
import com.laichushu.book.bean.netbean.PersonalCentre_Parmet;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.retrofit.ApiCallback;
import com.laichushu.book.ui.activity.EditMyselfeInforActivity;
import com.laichushu.book.ui.activity.ManageWorksActivity;
import com.laichushu.book.ui.activity.MyBookCastActivity;
import com.laichushu.book.ui.base.BasePresenter;
import com.laichushu.book.ui.base.MvpFragment2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.SharePrefManager;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;

/**
 * 我的
 * Created by wangtong on 2016/10/17.
 */
public class MineFragment extends MvpFragment2 implements View.OnClickListener {
    private TextView tvTitle, tvMineName, tvMinebookNum;
    private ImageView ivMineHead;
    private RelativeLayout rlHead, rlManage, rlBookCast, rlWallet, rlService, rlGeneralSetting, rlAdvice;
    private PersonalCentreResult res = new PersonalCentreResult();
    private UpdateReceiver mUpdateReceiver;

    @Override
    protected BasePresenter createPresenter() {

        return null;
    }

    @Override
    public View createSuccessView() {
        View mRootView = UIUtil.inflate(R.layout.fragment_mine);
        mRootView.findViewById(R.id.iv_title_finish).setVisibility(View.GONE);
        tvTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        ivMineHead = (ImageView) mRootView.findViewById(R.id.iv_minaHead);
        tvMineName = (TextView) mRootView.findViewById(R.id.tv_mineNickName);
        tvMinebookNum = (TextView) mRootView.findViewById(R.id.tv_mineBookNum);
        rlHead = (RelativeLayout) mRootView.findViewById(R.id.rl_head);
        rlManage = (RelativeLayout) mRootView.findViewById(R.id.rl_manage);
        rlBookCast = (RelativeLayout) mRootView.findViewById(R.id.rl_bookCast);
        rlWallet = (RelativeLayout) mRootView.findViewById(R.id.rl_Wallet);
        rlService = (RelativeLayout) mRootView.findViewById(R.id.rl_service);
        rlGeneralSetting = (RelativeLayout) mRootView.findViewById(R.id.rl_GeneralSetting);
        rlAdvice = (RelativeLayout) mRootView.findViewById(R.id.rl_Advice);
        //initListener
        rlHead.setOnClickListener(this);
        rlManage.setOnClickListener(this);
        rlBookCast.setOnClickListener(this);
        rlWallet.setOnClickListener(this);
        rlService.setOnClickListener(this);
        rlGeneralSetting.setOnClickListener(this);
        rlAdvice.setOnClickListener(this);


        tvTitle.setText("个人中心");
        return mRootView;
    }

    @Override
    protected void initData() {
        registerPlayerReceiver();
        super.initData();
        addSubscription(apiStores.getPersonalDetails(new PersonalCentre_Parmet(SharePrefManager.getUserId())), new ApiCallback<PersonalCentreResult>() {
            @Override
            public void onSuccess(PersonalCentreResult result) {
                if (result.getSuccess()) {
                    res = result;
                    GlideUitl.loadRandImg(mActivity, result.getPhoto(), ivMineHead);
                    tvMineName.setText("  "+result.getNickName());
                    tvMinebookNum.setText(result.getArticleCount()+"部  ");
                    refreshPage(LoadingPager.PageState.STATE_SUCCESS);
                } else {
                    ToastUtil.showToast(result.getErrMsg());
                    refreshPage(LoadingPager.PageState.STATE_ERROR);
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                refreshPage(LoadingPager.PageState.STATE_ERROR);
            }

            @Override
            public void onFinish() {

            }
        });
        refreshPage(LoadingPager.PageState.STATE_SUCCESS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_head:
                Intent editAct = new Intent(mActivity, EditMyselfeInforActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("result", res);
                editAct.putExtras(bundle);
                startActivity(editAct);
                break;
            case R.id.rl_manage:
                //作品管理
                Bundle manageBundle = new Bundle();
                UIUtil.openActivity(mActivity, ManageWorksActivity.class, manageBundle);
                break;
            case R.id.rl_bookCast:
                //我的书架
                Bundle bookCastBundle = new Bundle();
                UIUtil.openActivity(mActivity, MyBookCastActivity.class, bookCastBundle);
                break;
            case R.id.rl_Wallet:
                break;
            case R.id.rl_service:
                break;
            case R.id.rl_GeneralSetting:
                break;
            case R.id.rl_Advice:
                break;
        }
    }

    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConstantValue.ACTION_UPDATE_DATA.equals(action)) {
                initData();
                ToastUtil.showToast("update date!");
            }
        }
    }

    private void registerPlayerReceiver() {
        if (mUpdateReceiver == null) {
            mUpdateReceiver = new UpdateReceiver();

            IntentFilter filter = new IntentFilter();
            filter.addCategory(getActivity().getPackageName());

            filter.addAction(ConstantValue.ACTION_UPDATE_DATA);
            mActivity.registerReceiver(mUpdateReceiver, filter);
        }
    }
}