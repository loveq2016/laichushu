package com.sofacity.laichushu.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;

import com.sofacity.laichushu.R;
import com.sofacity.laichushu.ui.activity.CampaignActivity;
import com.sofacity.laichushu.utils.UIUtil;

import java.util.ArrayList;

/**
 * 参加活动对话框
 * Created by wangtong on 2016/10/26.
 */
public class JoinActivityAdapter extends BaseAdapter {
    private ArrayList<CampaignActivity.Text> mData;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public JoinActivityAdapter(ArrayList<CampaignActivity.Text> mData,int position) {
        this.mData = mData;
        this.position = position;
    }

    @Override
    public int getCount() {
        return mData == null?0:mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = UIUtil.inflate(R.layout.item_join);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        CampaignActivity.Text text = mData.get(position);
        holder.nameRbn.setText(text.getName());
        if (text.ischeck()){
            holder.nameRbn.setChecked(true);
        }else {
            holder.nameRbn.setChecked(false);
        }
        holder.nameRbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mData.size(); i++) {
                    if (i==position){
                        mData.get(i).setIscheck(true);
                        setPosition(position);
                    }else {
                        mData.get(i).setIscheck(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    private static class ViewHolder{
        RadioButton nameRbn;

        public ViewHolder(View convertView) {
            nameRbn = (RadioButton) convertView.findViewById(R.id.rbn_name);
        }
    }
}