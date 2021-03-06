package com.laichushu.book.ui.adapter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.netbean.HomeUseDyrResult;
import com.laichushu.book.global.ConstantValue;
import com.laichushu.book.mvp.msg.topic.topicmanage.TopicManagePresenter;
import com.laichushu.book.ui.activity.TopicDetilActivity;
import com.laichushu.book.ui.activity.TopicManageActivity;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.UIUtil;

import java.util.List;

/**
 * Created by PCPC on 2016/12/21.
 */

public class TopicManageAdapter extends RecyclerView.Adapter<TopicManageAdapter.ViewHolder> {
    private TopicManageActivity context;
    private List<HomeUseDyrResult.DataBean> dataBeen;
    private TopicManagePresenter topicPagePresener;
    private String type = null;
    private int currentNum = 0;

    public TopicManageAdapter(TopicManageActivity context, List<HomeUseDyrResult.DataBean> dataBean, TopicManagePresenter topicPagePresener) {
        this.context = context;
        this.dataBeen = dataBean;
        this.topicPagePresener = topicPagePresener;
    }

    @Override
    public TopicManageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = UIUtil.inflate(R.layout.item_mechanis_topic_manage);
        return new TopicManageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TopicManageAdapter.ViewHolder holder, final int position) {
        GlideUitl.loadRandImg(context, dataBeen.get(position).getCreaterPhoto(), holder.ivImg);
        holder.tvShopName.setText(dataBeen.get(position).getCreatUserName());
        holder.tvTime.setText(dataBeen.get(position).getCreateDate());
        holder.tvTitle.setText(dataBeen.get(position).getTitle());
        holder.tvTitleContent.setText(dataBeen.get(position).getContent());
        currentNum = dataBeen.get(position).getBrowseNum();
        if (dataBeen.get(position).isCollect()) {
            holder.tvCollect.setText("已收藏(" + dataBeen.get(position).getCollectNum() + ")");
            Drawable drawable = context.getResources().getDrawable(R.drawable.icon_praise_yes2x);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvCollect.setCompoundDrawables(drawable, null, null, null);
        } else {
            holder.tvCollect.setText("收藏(" + dataBeen.get(position).getCollectNum() + ")");
            Drawable drawable = context.getResources().getDrawable(R.drawable.icon_praise_no2x);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvCollect.setCompoundDrawables(drawable, null, null, null);
        }
        holder.llCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataBeen.get(position).isCollect()) {
                    //添加收藏
//                    currentNum++;
                    type = "0";
                    Drawable drawable = context.getResources().getDrawable(R.drawable.icon_praise_yes2x);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.tvCollect.setCompoundDrawables(drawable, null, null, null);
                    topicPagePresener.loadCollectSaveDate(dataBeen.get(position).getId(), ConstantValue.COLLECTTOPIC_TYPE, type, dataBeen, position);
//                    holder.tvCollect.setText("已收藏(" + currentNum + ")");
//                    dataBeen.get(position).setCollect(true);
                } else {
                    //取消收藏
//                    currentNum--;
                    type = "1";
                    Drawable drawable = context.getResources().getDrawable(R.drawable.icon_praise_no2x);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.tvCollect.setCompoundDrawables(drawable, null, null, null);
                    topicPagePresener.loadCollectSaveDate(dataBeen.get(position).getId(), ConstantValue.COLLECTTOPIC_TYPE, type, dataBeen, position);
//                    holder.tvCollect.setText("收藏(" + currentNum + ")");
//                    dataBeen.get(position).setCollect(false);
                }
            }
        });

        holder.llScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看话题详情
                Bundle topic = new Bundle();
                topic.putString("type", "homepage");
                topic.putString("tag", "top");
                topic.putSerializable("topBean", dataBeen.get(position));
                UIUtil.openActivity(context, TopicDetilActivity.class, topic);
            }
        });
        holder.ivDeleteTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除机构话题
                topicPagePresener.loadDeleteTopicManage(dataBeen.get(position).getId());
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

    public void refreshAdapter(List<HomeUseDyrResult.DataBean> listData) {
        dataBeen.clear();
        if (listData.size() > 0) {
            dataBeen.addAll(listData);
        }
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvShopName, tvTime, tvTitle, tvTitleContent, tvMsg, tvCollect, tvScan;
        public final ImageView ivImg, ivDeleteTopic;
        public final View root;
        public final LinearLayout llCollection, llScan;

        public ViewHolder(View root) {
            super(root);
            tvShopName = (TextView) root.findViewById(R.id.tv_dyName);
            tvTime = (TextView) root.findViewById(R.id.tv_dyTime);
            tvTitle = (TextView) root.findViewById(R.id.tv_dyTitleName);
            tvTitleContent = (TextView) root.findViewById(R.id.tv_dyTitleContent);
            tvMsg = (TextView) root.findViewById(R.id.tv_msg);
            tvCollect = (TextView) root.findViewById(R.id.tv_dyCollection);
            tvScan = (TextView) root.findViewById(R.id.tv_scan);
            ivImg = (ImageView) root.findViewById(R.id.iv_dyHead);
            ivDeleteTopic = (ImageView) root.findViewById(R.id.iv_topicDelete);
            llCollection = (LinearLayout) root.findViewById(R.id.ll_collection);
            llScan = (LinearLayout) root.findViewById(R.id.ll_scan);
            this.root = root;
        }
    }

    public void setDataBeen(List<HomeUseDyrResult.DataBean> dataBeen) {
        this.dataBeen = dataBeen;
        notifyDataSetChanged();
    }
}
