package com.laichushu.book.ui.activity;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.JsonBean.RewardResult;
import com.laichushu.book.bean.netbean.MyTabStrip;
import com.laichushu.book.bean.netbean.SignStateResult;
import com.laichushu.book.event.RefrushMineEvent;
import com.laichushu.book.event.RefurshWriteFragmentEvent;
import com.laichushu.book.mvp.home.HomeHotModel;
import com.laichushu.book.mvp.write.WritePresenter;
import com.laichushu.book.mvp.write.WriteView;
import com.laichushu.book.ui.adapter.WriteBookAdapter;
import com.laichushu.book.ui.base.MvpActivity2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.LoggerUtil;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ManageWorksActivity extends MvpActivity2<WritePresenter> implements WriteView, View.OnClickListener {
    private ImageView ivBack;
    private TextView tvTitle;
    private WriteBookAdapter writeBookAdapter;
    private PullLoadMoreRecyclerView mRecyclerView;
    private ArrayList<HomeHotModel.DataBean> mData = new ArrayList<>();
    private boolean isLoad = true;
    private ArrayList<MyTabStrip> mStrip = new ArrayList<>();
    private int img[] = {R.drawable.icon_draft, R.drawable.icon_material, R.drawable.icon_submission, R.drawable.icon_publishl, R.drawable.icon_delete, R.drawable.icon_sign2x};
    private String title[] = {"编辑目录", "编辑素材", "删除", "发表", "投稿", "签约状态"};
    //-------------
    private SignStateResult model = new SignStateResult();
    final List<String> list = new ArrayList<>();//出版社
    final List<String> editList = new ArrayList<>();//编辑
    private String pressId, editId;
    private int currentPos;

    @Override
    protected WritePresenter createPresenter() {
        return new WritePresenter(this);
    }

    @Override
    protected View createSuccessView() {
        View manageView = UIUtil.inflate(R.layout.activity_manage_works);
        EventBus.getDefault().register(this);
        ivBack = ((ImageView) manageView.findViewById(R.id.iv_title_finish));
        tvTitle = ((TextView) manageView.findViewById(R.id.tv_title));
        LinearLayout addNewBookLay = (LinearLayout) manageView.findViewById(R.id.lay_add_newbook);
        addNewBookLay.setOnClickListener(this);
        mRecyclerView = (PullLoadMoreRecyclerView) manageView.findViewById(R.id.manage_book_list);
        mRecyclerView.setLinearLayout();
        mRecyclerView.setScrollbarFadingEnabled(false);
        mRecyclerView.setPushRefreshEnable(false);
        mRecyclerView.setPullRefreshEnable(false);
        return manageView;
    }

    @Override
    protected void initData() {
        super.initData();
        //初始化strip列表
        mStrip.clear();
        for (int i = 0; i < 6; i++) {
            mStrip.add(new MyTabStrip(title[i], img[i]));
        }

        tvTitle.setText("作品管理");
        ivBack.setOnClickListener(this);
        if (isLoad) {//只执行一次
            mvpPresenter.getArticleBookList();
        } else {
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
        }
        writeBookAdapter = new WriteBookAdapter(mData, mActivity, mvpPresenter, mStrip);
        mRecyclerView.setAdapter(writeBookAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_finish:
                this.finish();
                break;
            case R.id.lay_add_newbook://创建新书
                UIUtil.openActivity(mActivity, CreateNewBookActivity.class);
                break;
        }
    }

    @Override
    public void getDataSuccess(HomeHotModel model) {
        if (model.isSuccess()) {
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            mData.addAll(model.getData());
            writeBookAdapter.setmData(mData);
            writeBookAdapter.notifyDataSetChanged();
            isLoad = false;
        } else {
            refreshPage(LoadingPager.PageState.STATE_ERROR);
            refurshData();
        }
    }

    @Override
    public void getSignEditorDataSuccess(RewardResult model) {
        if (model.isSuccess()) {
            ToastUtil.showToast("修改成功");
        } else {
            ToastUtil.showToast("修改失败");
        }
    }

    @Override
    public void articleVote(RewardResult model) {
        if (model.isSuccess()) {
            ToastUtil.showToast("投稿成功");
        } else {
            ToastUtil.showToast("投稿失败");
        }
    }

    @Override
    public void getSignStateDeteSuccess(final SignStateResult model, final String articleId) {
        if (model.isSuccess()) {
            this.model = model;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity, R.style.DialogStyle);
            final AlertDialog alertDialog = dialogBuilder.create();
            if (alertDialog.isShowing()) {
                return;
            }
            View customerView = UIUtil.inflate(R.layout.dialog_signstate);
            final Spinner spPress = (Spinner) customerView.findViewById(R.id.sp_press);
            final Spinner spEdit = (Spinner) customerView.findViewById(R.id.sp_edit);
            final TextView tvCancle = (TextView) customerView.findViewById(R.id.tv_cancel);
            final TextView tvSubmit = (TextView) customerView.findViewById(R.id.tv_submit);

            list.clear();
            for (int i = 0; i < model.getData().size(); i++) {
                list.add(model.getData().get(i).getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, R.layout.spiner_item_layout, list);
            adapter.setDropDownViewResource(R.layout.spiner_item_layout);
            final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mActivity, R.layout.spiner_item_layout, editList);
            adapter2.setDropDownViewResource(R.layout.spiner_item_layout);
            spPress.setAdapter(adapter);
            spEdit.setAdapter(adapter2);
            spPress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    editList.clear();
                    for (int i = 0; i < model.getData().get(position).getEditors().size(); i++) {
                        editList.add(model.getData().get(position).getEditors().get(i).getNickName());
                    }
                    pressId = model.getData().get(position).getId();

                    if (null != model.getData().get(position).getEditors() && model.getData().get(position).getEditors().size() > 0) {
                        editId = model.getData().get(position).getEditors().get(0).getId();
                    } else {
                        editId = null;
                    }

                    currentPos = position;
                    adapter2.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    editId = model.getData().get(currentPos).getEditors().get(position).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            tvCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            tvSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(pressId) && !TextUtils.isEmpty(editId)) {
                        mvpPresenter.getSignEditorDeta(pressId, articleId, editId);
                    } else {
                        ToastUtil.showToast("修改失败");
                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog.setView(customerView);
            alertDialog.show();
            WindowManager m = mActivity.getWindowManager();
            Display display = m.getDefaultDisplay();  //为获取屏幕宽、高
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            alertDialog.getWindow().setLayout(display.getWidth() - UIUtil.dip2px(60), display.getHeight() / 2- UIUtil.dip2px(80));
            alertDialog.getWindow().setWindowAnimations(R.style.periodpopwindow_anim_style);
            alertDialog.show();
        } else {
            ToastUtil.showToast("获取数据失败");
        }

    }

    @Override
    public void deleteNewBook(RewardResult model, int position) {
        if (model.isSuccess()) {
            ToastUtil.showToast("删除成功");
            mData.remove(position);
            writeBookAdapter.setmData(mData);
            writeBookAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToast("删除失败");
        }
    }

    @Override
    public void updateBookPermission(RewardResult model) {
        if (model.isSuccess()) {
            ToastUtil.showToast("修改权限成功");
        } else {
            ToastUtil.showToast("修改权限失败");
        }
    }

    @Override
    public void publishNewBook(RewardResult model, int index, String type) {
        if (model.isSuccess()) {
            if (type.equals("1")) {
                mData.get(index).setMake(false);
            } else {
                mData.get(index).setMake(true);
            }
            writeBookAdapter.setmData(mData);
            writeBookAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToast("发表失败");
        }
    }

    @Override
    public void getDataFail(String msg) {
        refreshPage(LoadingPager.PageState.STATE_ERROR);
        refurshData();
        LoggerUtil.e(msg);
    }

    @Override
    public void getDataFail2(String msg) {
        ToastUtil.showToast("删除失败");
        LoggerUtil.e(msg);
        hideLoading();
    }

    @Override
    public void getDataFail3(String msg) {
        ToastUtil.showToast("投稿失败");
        LoggerUtil.e(msg);
        hideLoading();
    }

    @Override
    public void getDataFail4(String msg) {
        ToastUtil.showToast("发表失败");
        LoggerUtil.e(msg);
        hideLoading();
    }

    @Override
    public void showLoading() {
        showProgressDialog();
    }

    @Override
    public void hideLoading() {
        dismissProgressDialog();
    }

    public void refurshData() {
        mPage.setmListener(new LoadingPager.ReLoadDataListenListener() {
            @Override
            public void reLoadData() {
                refreshPage(LoadingPager.PageState.STATE_LOADING);
                mData.clear();
                mvpPresenter.getArticleBookList();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefurshWriteFragmentEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (event.isRefursh()) {
            // TODO: 2016/11/17 刷新
            mData.clear();
            mvpPresenter.getArticleBookList();
        }
    }

    @Override
    public void finish() {
        super.finish();
        EventBus.getDefault().postSticky(new RefrushMineEvent(true));
    }
}
