package com.laichushu.book.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;
import com.laichushu.book.R;
import com.laichushu.book.bean.JsonBean.RewardResult;
import com.laichushu.book.bean.netbean.PersonalCentreResult;
import com.laichushu.book.db.Cache_Json;
import com.laichushu.book.db.Cache_JsonDao;
import com.laichushu.book.db.DaoSession;
import com.laichushu.book.global.BaseApplication;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.retrofit.ApiCallback;
import com.laichushu.book.ui.base.BasePresenter;
import com.laichushu.book.ui.base.MvpActivity2;
import com.laichushu.book.ui.widget.LoadingPager;
import com.laichushu.book.utils.DateUtil;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.SharePrefManager;
import com.laichushu.book.utils.ToastUtil;
import com.laichushu.book.utils.UIUtil;
import com.pickerview.OptionsPopupWindow;
import com.pickerview.TimePopupWindow;
import com.yanzhenjie.album.Album;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * 个人中心--编辑个人信息
 * 2016年11月24日11:25:54
 */
public class EditMyselfeInforActivity extends MvpActivity2 implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ImageView ivBack, ivHead;
    private TextView tvTitle, tvRight, tvIdCard, tvSex, edBirthday;
    private EditText edNickName, edCity, edSign;
    private PersonalCentreResult resultData = new PersonalCentreResult();
    //    private Pop_Syllabus_Date pop_PlayPartner_Date;
    private RelativeLayout rlIdCard, rlHead;
    private int ACTIVITY_REQUEST_SELECT_PHOTO = 100;
    private File photoFile;
    //时间选择器
    private TimePopupWindow timePopupWindow;
    private OptionsPopupWindow sexPopupWindow;
    private PopupWindow sexPopWindow;
    private Button submit, cancle;
    private RadioGroup rgSex;
    private RadioButton tvMan, tvFeman;
    private String sexMsg;
    private Cache_JsonDao cache_jsonDao;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public View createSuccessView() {

        View inflate = UIUtil.inflate(R.layout.activity_edit_myselfe_infor);
        ivBack = ((ImageView) inflate.findViewById(R.id.iv_title_finish));
        ivHead = ((ImageView) inflate.findViewById(R.id.iv_editHead));
        tvTitle = ((TextView) inflate.findViewById(R.id.tv_title));
        tvRight = ((TextView) inflate.findViewById(R.id.tv_title_right));
        tvIdCard = ((TextView) inflate.findViewById(R.id.tv_idCardContent));
        edNickName = ((EditText) inflate.findViewById(R.id.ed_nickNameContent));
        tvSex = ((TextView) inflate.findViewById(R.id.ed_sexContent));
        edCity = ((EditText) inflate.findViewById(R.id.ed_areaContent));
        edSign = ((EditText) inflate.findViewById(R.id.ed_signatureContent));
        edBirthday = ((TextView) inflate.findViewById(R.id.ed_birthdayContent));
        rlIdCard = ((RelativeLayout) inflate.findViewById(R.id.rl_idCard));
        rlHead = ((RelativeLayout) inflate.findViewById(R.id.rl_editHeadImg));

        //initListener;
        ivBack.setOnClickListener(this);
        rlHead.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        tvSex.setOnClickListener(this);
        edBirthday.setOnClickListener(this);
        rlIdCard.setOnClickListener(this);
        return inflate;
    }

    @Override
    protected void initData() {
        super.initData();
        initPersonInfor();
        tvTitle.setText("编辑个人信息");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成");
        edBirthday.setInputType(InputType.TYPE_NULL);
        refreshPage(LoadingPager.PageState.STATE_SUCCESS);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_finish:
                this.finish();
                break;
            case R.id.tv_title_right:
                //测试
                edSign.setText("123456789");
                edCity.setText("01");
                if (!judgeAttrbute()) {
                    return;
                }
                ArrayMap<String, RequestBody> params = new ArrayMap<>();
                RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), SharePrefManager.getUserId().toString());
                RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), edNickName.getText().toString());
                RequestBody requestBody3 = RequestBody.create(MediaType.parse("multipart/form-data"), tvSex.getText().toString());
                RequestBody requestBody4 = RequestBody.create(MediaType.parse("multipart/form-data"), edCity.getText().toString());
                RequestBody requestBody5 = RequestBody.create(MediaType.parse("multipart/form-data"), edSign.getText().toString());
                RequestBody requestBody6 = RequestBody.create(MediaType.parse("multipart/form-data"), edBirthday.getText().toString());
                RequestBody requestBody7 = RequestBody.create(MediaType.parse("multipart/form-data"), Compressor.getDefault(mActivity).compressToFile(photoFile));
                params.put("userId", requestBody1);
                params.put("nickName", requestBody2);
                params.put("sex", requestBody3);
                params.put("city", requestBody4);
                params.put("sign", requestBody5);
                params.put("birthday", requestBody6);
//                params.put("photoFile", requestBody7);
                addSubscription(apiStores.getUpdateDetails(params, requestBody7), new ApiCallback<RewardResult>() {
                    @Override
                    public void onSuccess(RewardResult result) {
                        if (result.isSuccess()) {
                            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
                            ToastUtil.showToast("success");
                            mActivity.finish();
//                            updateDate(url);
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

                break;
            case R.id.ed_sexContent:
//                setPopWindow();
                setSexPopWindow();
                break;
            case R.id.ed_birthdayContent:
//                showBirthdayPop();
                setTimePopupwindow();
                break;
            case R.id.rl_idCard:
                Bundle bundle = new Bundle();
                bundle.putSerializable("idcard", resultData);
                UIUtil.openActivity(this, IndentityAuthenActivity.class, bundle);
                break;
            case R.id.rl_editHeadImg:
                openAlertDialog();
                break;

            case R.id.sex_btnSubmit:
                sexPopWindow.dismiss();
                if (!TextUtils.isEmpty(sexMsg)) {
                    tvSex.setText(sexMsg);
                }

                break;
            case R.id.sex_btnCancel:
                sexPopWindow.dismiss();
                break;
        }

    }


    /**
     * 初始化数据
     */
    private void initPersonInfor() {
        resultData = (PersonalCentreResult) getIntent().getSerializableExtra("result");
        if (resultData != null) {
            GlideUitl.loadRandImg(mActivity, resultData.getPhoto(), ivHead);
            edNickName.setText(resultData.getNickName());
            if (TextUtils.isEmpty(resultData.getSex())) {
                tvSex.setText("男");
            } else {
                tvSex.setText(resultData.getSex());
            }
            if (!TextUtils.isEmpty(resultData.getBirthday())) {
                edBirthday.setText(resultData.getBirthday().toString());
            }
            if (!TextUtils.isEmpty(resultData.getCity())) {
                edCity.setText(resultData.getCity());
            }
            if (!TextUtils.isEmpty(resultData.getAtteStatus())) {
                switch (Integer.parseInt(resultData.getAtteStatus())) {
                    case 1:
                        tvIdCard.setText("未认证");
                        break;
                    case 2:
                        tvIdCard.setText("认证中");
                        break;
                    case 3:
                        tvIdCard.setText("认证失败");
                        break;
                    case 4:
                        tvIdCard.setText("认证通过");
                        break;
                }
            }

            edSign.setText(resultData.getSign());
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
        } else {
            ToastUtil.showToast("获取信息失败！");
            refreshPage(LoadingPager.PageState.STATE_SUCCESS);
        }

    }

    /**
     * 判断是否录入为空
     */
    private boolean judgeAttrbute() {

        if (TextUtils.isEmpty(edNickName.getText())) {
            ToastUtil.showToast("请输入昵称!");
            return false;
        }
        if (TextUtils.isEmpty(tvSex.getText())) {
            ToastUtil.showToast("强选择输入性别!");
            return false;
        }
        if (TextUtils.isEmpty(edCity.getText())) {
            ToastUtil.showToast("请输入您所在城市!");
            return false;
        }
        if (TextUtils.isEmpty(edBirthday.getText())) {
            ToastUtil.showToast("请输入出生日期!");
            return false;
        }
        if (TextUtils.isEmpty(edNickName.getText())) {
            ToastUtil.showToast("请输入个性签名!");
            return false;
        }
        return true;
    }

    /**
     * 设置性别
     */
    public void setPopWindow() {
        View sexView = UIUtil.inflate(R.layout.pop_sex);
        submit = (Button) sexView.findViewById(R.id.sex_btnSubmit);
        cancle = (Button) sexView.findViewById(R.id.sex_btnCancel);
        rgSex = (RadioGroup) sexView.findViewById(R.id.rg_sex);
        tvMan = (RadioButton) sexView.findViewById(R.id.sex_man);
        tvFeman = (RadioButton) sexView.findViewById(R.id.sex_femal);
        rgSex.setOnCheckedChangeListener(this);
        submit.setOnClickListener(this);
        cancle.setOnClickListener(this);
        sexPopWindow = new PopupWindow(sexView, WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        sexPopWindow.setFocusable(true);
        sexPopWindow.setOutsideTouchable(true);
        sexPopWindow.showAtLocation(tvSex, Gravity.BOTTOM, 0, 0);
        sexPopWindow.setAnimationStyle(R.style.timepopwindow_anim_style);
    }

    /**
     * 设置性别
     */
    public void setSexPopWindow() {
        final ArrayList<String> sexData = new ArrayList<>();
        sexData.clear();
        sexData.add("男");
        sexData.add("女");
        sexPopupWindow = new OptionsPopupWindow(this);
        sexPopupWindow.setPicker(sexData);
        sexPopupWindow.setCyclic(true);
        sexPopupWindow.setTitleBackgroundColor(mActivity.getResources().getColor(R.color.auditing));
        sexPopupWindow.setTitle("性别");
        sexPopupWindow.setSelectOptions(Gravity.CENTER_HORIZONTAL);
        sexPopupWindow.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                tvSex.setText(sexData.get(options1));
            }
        });
        sexPopupWindow.showAtLocation(tvSex, Gravity.BOTTOM, 0, 0);
    }

    //选择日期
    private void setTimePopupwindow() {
        timePopupWindow = new TimePopupWindow(this, TimePopupWindow.Type.YEAR_MONTH_DAY);
//        timePopupWindow.setti
        timePopupWindow.setOnTimeSelectListener(new TimePopupWindow.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                edBirthday.setText(DateUtil.getFormatDateTime(date));
            }
        });
        timePopupWindow.showAtLocation(edBirthday, Gravity.BOTTOM, 0, 0, null);
    }

    /**
     * 通知fragment更新数据
     */
    private void updateDate(String imgUrl) {
        //更新本地数据
        DaoSession daoSession = BaseApplication.getDaoSession(mActivity);
        cache_jsonDao = daoSession.getCache_JsonDao();
        QueryBuilder<Cache_Json> userQueryBuilder = cache_jsonDao.queryBuilder();
        QueryBuilder<Cache_Json> builder = userQueryBuilder.where(Cache_JsonDao.Properties.Inter.eq("PersonalDetails"));
        Query<Cache_Json> build = builder.build();
        List<Cache_Json> cache_jsons = build.list();
        Cache_Json cache_json = cache_jsons.get(0);
        PersonalCentreResult json = new Gson().fromJson(cache_json.getJson(), PersonalCentreResult.class);
        json.setNickName(edNickName.getText().toString());
        json.setSex(tvSex.getText().toString());
        json.setBirthday(edBirthday.getText().toString());
        json.setCity(edCity.getText().toString());
        json.setSign(edSign.getText().toString());

//        json.setPhoto();
        //
        cache_json.setJson(new Gson().toJson(json));
        cache_jsonDao.update(cache_json);
        Intent intent = new Intent(ConstantValue.ACTION_UPDATE_DATA);
        intent.addCategory(mActivity.getPackageName());
        mActivity.sendBroadcast(intent);
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
            List<String> imagesPath = Album.parseResult(data);
            if (imagesPath != null && imagesPath.size() > 0) {
                String path = imagesPath.get(0);
                GlideUitl.loadRandImg(mActivity, path, ivHead);
                //压缩图片
                photoFile = new File(path);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.sex_man:
                sexMsg = "男";
                break;
            case R.id.sex_femal:
                sexMsg = "女";
                break;
        }

    }
}
