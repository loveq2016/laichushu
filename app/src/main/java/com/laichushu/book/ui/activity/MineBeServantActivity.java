package com.laichushu.book.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.JsonBean.RewardResult;
import com.laichushu.book.bean.netbean.FindServerInfoModel;
import com.laichushu.book.event.RefrushMineBeServiceEvent;
import com.laichushu.book.event.RefrushWalletEvent;
import com.laichushu.book.mvp.mine.mineservant.MineBeServantPresener;
import com.laichushu.book.mvp.mine.mineservant.MineBeServantView;
import com.laichushu.book.ui.base.MvpActivity2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.JudgeUtil;
import com.laichushu.book.utils.LoggerUtil;
import com.laichushu.book.utils.SharePrefManager;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;
import com.yanzhenjie.album.Album;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * 我的---成为服务者
 */
public class MineBeServantActivity extends MvpActivity2<MineBeServantPresener> implements MineBeServantView, View.OnClickListener {

    private ImageView ivBack;
    private TextView tvTitle, tvServantType, tvAgreement;
    private RelativeLayout rlNickName, rlPostOffice, rlJobTitle, rlEmail, rlIdProve, rlServantType;
    private LinearLayout llItem;
    private ImageView ivVisiting, vistingFront;
    private EditText edNickName, edPostOffice, edJobTitle, edEmail, edIdProve, edIntroduce;
    private Button btnSubmit;
    private CheckBox checkBox;
    private File visitingFile;
    private String curServantType = null;
    private int ACTIVITY_REQUEST_SELECT_PHOTO = 100;
    private String type = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                finish();
            }
        }
    };

    //TODO 判断邮箱格式
    @Override
    protected MineBeServantPresener createPresenter() {
        return new MineBeServantPresener(this);
    }

    @Override
    protected View createSuccessView() {
        View inflate = UIUtil.inflate(R.layout.activity_mine_be_servant);
        ivBack = ((ImageView) inflate.findViewById(R.id.iv_title_finish));
        tvTitle = ((TextView) inflate.findViewById(R.id.tv_title));
        tvAgreement = ((TextView) inflate.findViewById(R.id.tv_agreement));
        tvServantType = ((TextView) inflate.findViewById(R.id.tv_servantTypeContent));
        rlNickName = (RelativeLayout) inflate.findViewById(R.id.rl_servantName);
        rlPostOffice = (RelativeLayout) inflate.findViewById(R.id.rl_servantPostOffice);
        rlJobTitle = (RelativeLayout) inflate.findViewById(R.id.rl_servantPosition);
        rlEmail = (RelativeLayout) inflate.findViewById(R.id.rl_servantEmails);
        rlIdProve = (RelativeLayout) inflate.findViewById(R.id.rl_servantWitness);
        rlServantType = (RelativeLayout) inflate.findViewById(R.id.rl_servantType);
        ivVisiting = (ImageView) inflate.findViewById(R.id.iv_VisitingCard);
        vistingFront = (ImageView) inflate.findViewById(R.id.iv_idCardFront);

        edNickName = (EditText) inflate.findViewById(R.id.ed_servantName);
        edPostOffice = (EditText) inflate.findViewById(R.id.ed_servantPostOffice);
        edJobTitle = (EditText) inflate.findViewById(R.id.ed_servantPosition);
        edEmail = (EditText) inflate.findViewById(R.id.ed_servantEmails);
        edIdProve = (EditText) inflate.findViewById(R.id.ed_servantWitness);
        edIntroduce = (EditText) inflate.findViewById(R.id.ed_servantIntroduce);
        btnSubmit = (Button) inflate.findViewById(R.id.btn_servantSubmit);
        checkBox = (CheckBox) inflate.findViewById(R.id.cb_ServantChkItem);

        llItem = (LinearLayout) inflate.findViewById(R.id.ll_item);
        return inflate;
    }

    @Override
    protected void initData() {
        super.initData();
        tvTitle.setText("成为服务者");
        type = getIntent().getStringExtra("type");

        ivBack.setOnClickListener(this);
        tvAgreement.setOnClickListener(this);
        if (null == type) {
            rlServantType.setOnClickListener(this);
            ivVisiting.setOnClickListener(this);
            btnSubmit.setOnClickListener(this);
        } else {
            //点击事件
            llItem.setClickable(false);
            //请求服务详情
            mvpPresenter.loadEditorInfoData(SharePrefManager.getUserId());
        }

        UIUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            }
        }, 30);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_finish:
                this.finish();
                break;
            case R.id.rl_servantType:
                //选择服务者类型
                openServantTypeDialog(mActivity, tvServantType);
                break;
            case R.id.iv_VisitingCard:
                //上传明信片
                openAlertDialog();
                break;
            case R.id.tv_agreement:
                //著作协议
                Bundle bundle = new Bundle();
                bundle.putString("type", "1");
                UIUtil.openActivity(mActivity, AgreementDetailsActivity.class, bundle);
                break;
            case R.id.btn_servantSubmit:
                //提交审核
                if (judgeData()) {
                    ArrayMap<String, RequestBody> params = new ArrayMap<>();
                    RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), SharePrefManager.getUserId().toString());
                    RequestBody nickName = RequestBody.create(MediaType.parse("multipart/form-data"), edNickName.getText().toString().trim());
                    RequestBody companyName = RequestBody.create(MediaType.parse("multipart/form-data"), edPostOffice.getText().toString().trim());
                    RequestBody jobTitle = RequestBody.create(MediaType.parse("multipart/form-data"), edJobTitle.getText().toString().trim());
                    RequestBody email = RequestBody.create(MediaType.parse("multipart/form-data"), edEmail.getText().toString().trim());
                    RequestBody idIprove = RequestBody.create(MediaType.parse("multipart/form-data"), edIdProve.getText().toString().trim());
                    RequestBody servantType = RequestBody.create(MediaType.parse("multipart/form-data"), curServantType);
                    RequestBody introduce = RequestBody.create(MediaType.parse("multipart/form-data"), edIntroduce.getText().toString().trim());
                    RequestBody visitingCard = RequestBody.create(MediaType.parse("multipart/form-data"), Compressor.getDefault(mActivity).compressToFile(visitingFile));

                    params.put("loginUserId", userId);
                    params.put("nickName", nickName);
                    params.put("companyName", companyName);
                    params.put("jobTitle", jobTitle);
                    params.put("email", email);
                    params.put("idProve", idIprove);
                    params.put("servantType", servantType);
                    params.put("introduce", introduce);

                    mvpPresenter.loadServantInfoData(params, visitingCard);
                }
                break;
        }
    }

    @Override
    public void getSaveServerInfoDataSuccess(RewardResult model) {
        dismissDialog();
        if (model.isSuccess()) {
            ToastUtil.showToast("请求已受理！");
            handler.sendEmptyMessageDelayed(1, 1700);
        } else {
            ToastUtil.showToast(model.getErrMsg());
        }
    }

    @Override
    public void getEditorInfoDataSuccess(FindServerInfoModel model) {
        if (model.isSuccess()) {
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
            if (null != model) {
                edNickName.setText(model.getData().getName());
                edPostOffice.setText(model.getData().getCompanyName());
                edJobTitle.setText(model.getData().getJobTitle());
                edEmail.setText(model.getData().getEmail());
                edIdProve.setText(model.getData().getIdProve());
                if (model.getData().getServiceType() == 1) {
                    tvServantType.setText("代笔");
                } else if (model.getData().getServiceType() == 2) {
                    tvServantType.setText("设计");
                }
                GlideUitl.loadImg(mActivity, model.getData().getPhoto(), ivVisiting.getWidth(), ivVisiting.getHeight(), ivVisiting);
                edIntroduce.setText(model.getData().getServiceIntroduce());
                btnSubmit.setClickable(false);
                edNickName.setFocusable(false);
                edPostOffice.setFocusable(false);
                edJobTitle.setFocusable(false);
                edEmail.setFocusable(false);
                edIdProve.setFocusable(false);
                edIntroduce.setFocusable(false);
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                btnSubmit.setBackgroundResource(R.drawable.shape_rectangle_normal_gray);
                vistingFront.setVisibility(View.INVISIBLE);
            }

        } else {
            ToastUtil.showToast(model.getErrMsg());
            refreshPage(LoadingPager.PageState.STATE_ERROR);
        }
    }

    @Override
    public void getDataFail(String msg) {
        LoggerUtil.toJson(msg);
    }

    @Override
    public void showDialog() {
        showProgressDialog();
    }

    @Override
    public void dismissDialog() {
        dismissProgressDialog();
    }

    public boolean judgeData() {
        if (TextUtils.isEmpty(edNickName.getText().toString().trim())) {
            ToastUtil.showToast("请输入昵称！");
            return false;
        }
        if (TextUtils.isEmpty(edPostOffice.getText().toString().trim())) {
            ToastUtil.showToast("请输入任职机构！");
            return false;
        }
        if (TextUtils.isEmpty(edJobTitle.getText().toString().trim())) {
            ToastUtil.showToast("请输入头衔或者职位！");
            return false;
        }
        if (TextUtils.isEmpty(edEmail.getText().toString().trim())) {
            ToastUtil.showToast("请输入常用邮箱！");
            return false;
        }
        if (!TextUtils.isEmpty(edEmail.getText().toString().trim())) {
            if (!JudgeUtil.isEmail(edEmail.getText().toString().trim())) {
                ToastUtil.showToast("邮箱格式不正确!");
                return false;
            }
        }


        if (TextUtils.isEmpty(edIdProve.getText().toString().trim())) {
            ToastUtil.showToast("请输入身份证明人！");
            return false;
        }
        if (TextUtils.isEmpty(tvServantType.getText().toString().trim())) {
            ToastUtil.showToast("请输入服务类型！");
            return false;
        }
        if (visitingFile == null) {
            ToastUtil.showToast("请上传名片！");
            return false;
        }
        if (TextUtils.isEmpty(edIntroduce.getText().toString().trim())) {
            ToastUtil.showToast("请编辑个人介绍！");
            return false;
        }
        if (!checkBox.isChecked()) {
            ToastUtil.showToast("您还没有同意我们的服务协议！");
            return false;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        EventBus.getDefault().postSticky(new RefrushMineBeServiceEvent(true));
    }

    /**
     * 打开相册
     */
    public void openAlertDialog() {

        Album.startAlbum(mActivity, ACTIVITY_REQUEST_SELECT_PHOTO
                , 1                                                         // 指定选择数量。
                , ContextCompat.getColor(mActivity, R.color.global)        // 指定Toolbar的颜色。
                , ContextCompat.getColor(mActivity, R.color.global));  // 指定状态栏的颜色。
    }

    /**
     * 得到选择的图片路径集合
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            List<String> imagesPath = null;
            if (imagesPath != null) {
                imagesPath.clear();
            }
            imagesPath = Album.parseResult(data);
            if (imagesPath != null && imagesPath.size() > 0) {
                String path = imagesPath.get(0);
                //压缩图片
                GlideUitl.loadImg(mActivity, path, ivVisiting.getWidth(), ivVisiting.getHeight(), ivVisiting);
                visitingFile = new File(path);
            }
        }
    }

    /**
     * 选择服务类型
     *
     * @param mActivity
     * @param tvServantType
     */
    public void openServantTypeDialog(final Activity mActivity, final TextView tvServantType) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity, R.style.DialogStyle);
        final AlertDialog alertDialog = dialogBuilder.create();
        View customerView = UIUtil.inflate(R.layout.dialog_servant_type);
        RadioGroup rgServant = (RadioGroup) customerView.findViewById(R.id.rg_servant);
        TextView tvCancel = (TextView) customerView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        });
        rgServant.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_ghost:
                        tvServantType.setText("代笔");
                        curServantType = "1";
                        break;
                    case R.id.rb_design:
                        tvServantType.setText("设计");
                        curServantType = "2";
                        break;
                }
                if (alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        });
        alertDialog.setView(customerView);
        alertDialog.show();
        WindowManager m = mActivity.getWindowManager();
        Display display = m.getDefaultDisplay();  //为获取屏幕宽、高
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.getWindow().setLayout(display.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setWindowAnimations(R.style.timepopwindow_anim_style);
        alertDialog.show();
    }
}
