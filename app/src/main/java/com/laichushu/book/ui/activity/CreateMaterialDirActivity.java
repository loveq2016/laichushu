package com.laichushu.book.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.JsonBean.RewardResult;
import com.laichushu.book.event.RefurshMaterialDirEvent;
import com.laichushu.book.mvp.write.createnewmaterialdir.CreateNewMaterialDirPresenter;
import com.laichushu.book.mvp.write.createnewmaterialdir.CreateNewMaterialDirView;
import com.laichushu.book.ui.base.MvpActivity2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.LoggerUtil;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 创建新素材文件夹
 * Created by wangtong on 2016/11/22.
 */

public class CreateMaterialDirActivity extends MvpActivity2<CreateNewMaterialDirPresenter> implements CreateNewMaterialDirView, View.OnClickListener {

    private TextView titleTv;
    private ImageView finishIv;
    private EditText materialEt;
    private Button createBtn;
    private String articleId;

    @Override
    protected CreateNewMaterialDirPresenter createPresenter() {
        return new CreateNewMaterialDirPresenter(this);
    }

    @Override
    protected View createSuccessView() {
        View mSuccessView = UIUtil.inflate(R.layout.activity_createnewmaterialdir);
        titleTv = (TextView) mSuccessView.findViewById(R.id.tv_title);
        finishIv = (ImageView) mSuccessView.findViewById(R.id.iv_title_finish);
        materialEt = (EditText) mSuccessView.findViewById(R.id.et_material);
        createBtn = (Button) mSuccessView.findViewById(R.id.btn_create);
        titleTv.setText("添加素材文件夹");
        finishIv.setOnClickListener(this);
        createBtn.setOnClickListener(this);
        return mSuccessView;
    }

    @Override
    protected void initData() {
        articleId = getIntent().getStringExtra("articleId");
        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            }
        }, 30);
    }
    @Override
    protected void initView() {
        super.initView();
        mPage.tvTitle.setText("添加素材文件夹");
    }
    /**
     * 获取资源文件夹
     *
     * @param model
     */

    @Override
    public void getCreateSourceMaterialDir(RewardResult model) {
        if (model.isSuccess()) {
            ToastUtil.showToast("创建成功");
            UIUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().postSticky(new RefurshMaterialDirEvent(true));
                    finish();
                }
            }, 1700);
        } else {
            ToastUtil.showToast("创建失败");
            LoggerUtil.toJson(model);
        }
    }

    @Override
    public void getDataFail(String msg) {
        hideLoading();
        LoggerUtil.e(msg);
        ToastUtil.showToast("创建失败");
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
        switch (v.getId()) {
            case R.id.btn_create:
                String foldName = materialEt.getText().toString();
                if (TextUtils.isEmpty(foldName)) {
                    ToastUtil.showToast("文件夹名字不能为空");
                } else {
                    mvpPresenter.createSourceMaterialDir(articleId, foldName);
                }
                break;
            case R.id.iv_title_finish:
                finish();
                break;
        }
    }
}
