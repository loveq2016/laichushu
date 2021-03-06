package com.laichushu.book.ui.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.netbean.FindEditorListModel;
import com.laichushu.book.mvp.find.eidt.findeditpage.FindEditPagePresenter;
import com.laichushu.book.ui.activity.FindEditMainPageActivity;
import com.laichushu.book.ui.activity.FindEditPageActivity;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.UIUtil;

import java.util.List;

/**
 * Created by PCPC on 2016/12/23.
 */

public class TotalRanKingAdapter extends RecyclerView.Adapter<TotalRanKingAdapter.ViewHolder> {
    private FindEditPageActivity context;
    private List<FindEditorListModel.DataBean> dataBeen;
    private FindEditPagePresenter findEditPagePresenter;

    public TotalRanKingAdapter(FindEditPageActivity context, List<FindEditorListModel.DataBean> dataBean, FindEditPagePresenter findEditPagePresenter) {
        this.context = context;
        this.dataBeen = dataBean;
        this.findEditPagePresenter = findEditPagePresenter;
    }

    @Override
    public TotalRanKingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = UIUtil.inflate(R.layout.item_find_ranking);
        return new TotalRanKingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TotalRanKingAdapter.ViewHolder holder, final int position) {
        GlideUitl.loadRandImg(context, dataBeen.get(position).getPhoto(), holder.ivImg);
        holder.tvTeamWork.setText(dataBeen.get(position).getCooperateNum() + "人已合作");
        holder.tvExperience.setText(dataBeen.get(position).getWorkingYears() + "年工作经验");
        holder.tvPublish.setText(dataBeen.get(position).getPress());
        holder.tvRealName.setText(dataBeen.get(position).getName());
        if (null != dataBeen.get(position).getLevel()) {
            switch (dataBeen.get(position).getLevel()) {
                case "1":
                    GlideUitl.loadImg(context, R.drawable.icon_gold_medal2x, holder.tvDegress);
                    break;
                case "2":
                    GlideUitl.loadImg(context, R.drawable.icon_silver_medal2x, holder.tvDegress);
                    break;
                case "3":
                    GlideUitl.loadImg(context, R.drawable.icon_copper_medal2x, holder.tvDegress);
                    break;
            }
        }
        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                跳转编辑主页
                Bundle bundle = new Bundle();
                bundle.putSerializable("userId", dataBeen.get(position).getId());
                UIUtil.openActivity(context, FindEditMainPageActivity.class, bundle);
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataBeen == null ? 0 : dataBeen.size();
    }

    public void refreshAdapter(List<FindEditorListModel.DataBean> listData) {
        dataBeen.clear();
        if (listData.size() > 0) {
            dataBeen.addAll(listData);
        }
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout rlItem;
        public final TextView tvTeamWork, tvExperience, tvPublish, tvRealName;
        public final ImageView ivImg, tvDegress;
        public final View root;

        public ViewHolder(View root) {
            super(root);
            rlItem = (RelativeLayout) root.findViewById(R.id.rl_item);
            ivImg = (ImageView) root.findViewById(R.id.iv_userHeadImg);
            tvTeamWork = (TextView) root.findViewById(R.id.tv_teamworkNumber);
            tvExperience = (TextView) root.findViewById(R.id.tv_experience);
            tvPublish = (TextView) root.findViewById(R.id.tv_publish);
            tvRealName = (TextView) root.findViewById(R.id.tv_realName);
            tvDegress = (ImageView) root.findViewById(R.id.tv_authorDegree);
            this.root = root;
        }
    }
}
