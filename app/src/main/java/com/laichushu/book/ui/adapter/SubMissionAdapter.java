package com.laichushu.book.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.bean.netbean.MessageCommentResult;
import com.laichushu.book.mvp.msg.messagecomment.MessageCommentPresenter;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.UIUtil;

import java.util.List;

/**
 * Created by PCPC on 2016/11/29.
 */

public class SubMissionAdapter extends RecyclerView.Adapter<SubMissionAdapter.ViewHolder> {
    private Activity context;
    private List<MessageCommentResult.DataBean> dataBeen;
    private MessageCommentPresenter messageCommentPresenter;

    public SubMissionAdapter(Activity context, List<MessageCommentResult.DataBean> dataBean, MessageCommentPresenter messageCommentPresenter) {
        this.context = context;
        this.dataBeen = dataBean;
        this.messageCommentPresenter = messageCommentPresenter;
    }

    @Override
    public SubMissionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = UIUtil.inflate(R.layout.item_msg_submission);
        return new SubMissionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubMissionAdapter.ViewHolder holder, final int position) {
        GlideUitl.loadImg(context, dataBeen.get(position).getCoverUrl(), holder.ivHead);
        holder.tvBookName.setText(dataBeen.get(position).getSourceName());
        holder.tvPubName.setText(dataBeen.get(position).getPressName());
        switch (dataBeen.get(position).getContributeStatus()) {
            case "0":
                holder.status.setText("未投稿");
                break;
            case "1":
                holder.status.setText("审批中");
                holder.status.setTextColor(context.getResources().getColor(R.color.red2));
                break;
            case "2":
                holder.status.setText("审批通过");
                holder.status.setTextColor(context.getResources().getColor(R.color.red2));
                break;
            case "3":
                holder.status.setText("审批被拒");
                holder.status.setTextColor(context.getResources().getColor(R.color.red2));
                break;
        }
        holder.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转图书详情？
                messageCommentPresenter.loadBookDetailsByid(dataBeen.get(position).getArticleId());
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = dataBeen.get(position).getId();
                dataBeen.remove(position);
                messageCommentPresenter.messageDeleteComment(position, id);
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

    public void refreshAdapter(List<MessageCommentResult.DataBean> listData) {
        dataBeen.clear();
        if (listData.size() > 0) {
            dataBeen.addAll(listData);
            this.notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout llItem;
        public final TextView tvPubName, tvBookName;
        public final ImageView ivHead, ivReplay, ivDelete;
        public Button status;
        public final View root;

        public ViewHolder(View root) {
            super(root);
            llItem = (LinearLayout) root.findViewById(R.id.ll_item);
            tvPubName = (TextView) root.findViewById(R.id.tv_publishName);
            tvBookName = (TextView) root.findViewById(R.id.tv_bookName);
            ivHead = (ImageView) root.findViewById(R.id.iv_subHead);
            ivReplay = (ImageView) root.findViewById(R.id.iv_replayMsg);
            ivDelete = (ImageView) root.findViewById(R.id.iv_subDeleteMsg);
            status = (Button) root.findViewById(R.id.btn_status);
            this.root = root;
        }
    }
}
