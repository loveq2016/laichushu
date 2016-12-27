package com.laichushu.book.mvp.findservicepage;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.laichushu.book.R;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.mvp.findeditpage.FindEditPageView;
import com.laichushu.book.ui.activity.FindEditPageActivity;
import com.laichushu.book.ui.activity.FindServicePageActivity;
import com.laichushu.book.ui.base.BasePresenter;
import com.laichushu.book.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PCPC on 2016/12/26.
 */

public class FindServicePagePresenter extends BasePresenter<FindServicePageView> {
    private FindServicePageActivity mActivity;
    private String pageSize = ConstantValue.PAGESIZE1;
    private String pageNo = "1";
    private String userId = ConstantValue.USERID;

    //初始化构造
    public FindServicePagePresenter(FindServicePageView view) {
        attachView(view);
        mActivity = (FindServicePageActivity) view;
    }
    /**
     * 全部排行
     *
     * @param mActicity
     * @param v
     */
    public void showRankingDialog(final Activity mActicity, CheckBox v) {
        View customerView = UIUtil.inflate(R.layout.dialog_mechanis_manage_item);
        final PopupWindow popupWindow = new PopupWindow(customerView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ListView listView = (ListView) customerView.findViewById(R.id.lv_item);
        List<String> data = new ArrayList<>();
        data.clear();
        data.add("工作年限");
        data.add("评论人数");
        data.add("合作人数");
        data.add("评论分数");
        ArrayAdapter adapter = new ArrayAdapter(mActicity, R.layout.spiner_item_layout, data);
        listView.setAdapter(adapter);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        int xPos = mActicity.getWindowManager().getDefaultDisplay().getWidth() / 4
                - v.getWidth() / 4;
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAsDropDown(v, xPos, 40);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //工作年限

                        break;
                    case 1:
                        //评论人数
                        break;
                    case 2:
                        //合作人数
                        break;
                    case 3:
                        //评论分数
                        break;
                }
                if (null != popupWindow) {
                    popupWindow.dismiss();
                }

            }
        });

    }
}