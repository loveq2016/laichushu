package com.laichushu.book.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.netbean.MyHomeModel;
import com.laichushu.book.mvp.bookcast.BookCastModle;
import com.laichushu.book.mvp.home.HomeHotModel;
import com.laichushu.book.ui.activity.BookDetailActivity;
import com.laichushu.book.ui.activity.MyBookCastActivity;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.UIUtil;

import java.util.List;

/**
 * Created by PCPC on 2016/11/18.
 */

public class MyBookCastAdapter extends RecyclerView.Adapter<MyBookCastAdapter.ViewHolder> {
    private MyBookCastActivity context;
    private List<HomeHotModel.DataBean> dataBeen;
    public MyBookCastAdapter(MyBookCastActivity context, List<HomeHotModel.DataBean> dataBean) {
        this.context = context;
        this.dataBeen = dataBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = UIUtil.inflate(R.layout.item_bookcast);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GlideUitl.loadImg(context, dataBeen.get(position).getCoverUrl(), holder.ivImg);
        holder.tvItem.setText(dataBeen.get(position).getArticleName());
        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                跳转图书详情页
                Bundle bundle = new Bundle();
                bundle.putParcelable("bean", dataBeen.get(position));
                UIUtil.openActivity(context, BookDetailActivity.class, bundle);
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

    public void refreshAdapter(List<HomeHotModel.DataBean> listData) {
        dataBeen.clear();
        if (listData.size() > 0) {
            dataBeen.addAll(listData);
            this.notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout llItem;
        public final TextView tvItem;
        public final ImageView ivImg;
        public final View root;

        public ViewHolder(View root) {
            super(root);
            llItem = (LinearLayout) root.findViewById(R.id.ll_item);
            tvItem = (TextView) root.findViewById(R.id.tv_item);
            ivImg = (ImageView) root.findViewById(R.id.iv_img);
            this.root = root;
        }
    }
}
