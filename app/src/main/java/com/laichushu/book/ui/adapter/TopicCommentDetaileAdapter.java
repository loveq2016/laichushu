package com.laichushu.book.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laichushu.book.R;
import com.laichushu.book.mvp.home.commentdetail.CommentDetailModle;
import com.laichushu.book.mvp.msg.topic.topicdetail.TopicDetailPresenter;
import com.laichushu.book.ui.base.BaseActivity;
import com.laichushu.book.utils.GlideUitl;
import com.laichushu.book.utils.UIUtil;

import java.util.ArrayList;

/**
 * 话题详情评论
 * Created by wangtong on 2016/11/28.
 */
public class TopicCommentDetaileAdapter extends RecyclerView.Adapter<TopicCommentDetaileAdapter.CommentViewHolder> {
    private BaseActivity mActivity;
    private ArrayList<CommentDetailModle.DataBean> mData;
    private TopicDetailPresenter topicDetailPresenter;

    public TopicCommentDetaileAdapter(BaseActivity mActivity, ArrayList<CommentDetailModle.DataBean> mData, TopicDetailPresenter topicDetailPresenter) {
        this.mActivity = mActivity;
        this.mData = mData;
        this.topicDetailPresenter = topicDetailPresenter;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = UIUtil.inflate(R.layout.item_comment_connet);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final CommentDetailModle.DataBean dataBean = mData.get(position);
        GlideUitl.loadRandImg(mActivity, dataBean.getPhoto(), holder.headIv);//头像
        holder.nameTv.setText(dataBean.getNickName());//用户名
        holder.contentTv.setText(dataBean.getContent());//评论内容
        holder.timeTv.setText(dataBean.getCommentTime());//创建时间
        holder.likeTv.setText(dataBean.getLikeNum() + "");//喜欢人数
//        holder.numberTv.setText(dataBean.getReplyNum() + "");//回复人数
        holder.numberTv.setVisibility(View.INVISIBLE);
        holder.commentIv.setVisibility(View.INVISIBLE);

        holder.inIv.setVisibility(View.INVISIBLE);
        if (dataBean.isIsLike()) {
            GlideUitl.loadImg(mActivity, R.drawable.icon_up_small_red, holder.likeIv);
        } else {
            GlideUitl.loadImg(mActivity, R.drawable.icon_up_small_normal, holder.likeIv);
        }
        holder.likeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataBean.isIsLike()) {
                    GlideUitl.loadImg(mActivity, R.drawable.icon_up_small_normal, holder.likeIv);
                    dataBean.setIsLike(false);
                    dataBean.setLikeNum(dataBean.getLikeNum() - 1);
                    holder.likeTv.setText(dataBean.getLikeNum() + "");
                    topicDetailPresenter.saveScoreLikeData(dataBean.getId(), "2", "1");
                } else {
                    GlideUitl.loadImg(mActivity, R.drawable.icon_up_small_red, holder.likeIv);
                    dataBean.setIsLike(true);
                    dataBean.setLikeNum(dataBean.getLikeNum() + 1);
                    holder.likeTv.setText(dataBean.getLikeNum() + "");
                    topicDetailPresenter.saveScoreLikeData(dataBean.getId(), "2", "0");
                }
            }
        });
//        holder.commentItemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: 2016/11/4  去评论详情
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("bean",dataBean);
//                UIUtil.openActivity(mActivity,CommentDetailActivity.class,bundle);
//            }
//        });
    }

    public ArrayList<CommentDetailModle.DataBean> getmData() {
        return mData;
    }

    public void setmData(ArrayList<CommentDetailModle.DataBean> mData) {
        this.mData = mData;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        public ImageView commentIv;
        public ImageView headIv;
        public TextView nameTv;
        public TextView contentTv;
        public TextView timeTv;
        public ImageView likeIv;
        public TextView likeTv;
        public TextView numberTv;
        public ImageView inIv;
        public View commentItemView;

        public CommentViewHolder(View commentItemView) {
            super(commentItemView);
            this.commentItemView = commentItemView;
            headIv = (ImageView) commentItemView.findViewById(R.id.iv_comment_head);
            nameTv = (TextView) commentItemView.findViewById(R.id.tv_comment_name);
            contentTv = (TextView) commentItemView.findViewById(R.id.tv_comment_content);
            timeTv = (TextView) commentItemView.findViewById(R.id.tv_comment_time);
            likeIv = (ImageView) commentItemView.findViewById(R.id.iv_comment_like);
            likeTv = (TextView) commentItemView.findViewById(R.id.tv_comment_like);
            numberTv = (TextView) commentItemView.findViewById(R.id.tv_comment_number);
            inIv = (ImageView) commentItemView.findViewById(R.id.iv_comment_in);
            commentIv = (ImageView) commentItemView.findViewById(R.id.iv_comment);
        }
    }
}
