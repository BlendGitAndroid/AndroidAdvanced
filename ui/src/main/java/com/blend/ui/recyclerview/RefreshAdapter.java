package com.blend.ui.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blend.ui.R;

import java.util.List;


public class RefreshAdapter extends RecyclerView.Adapter {

    private static final int TYPE_BODY = 1;
    private static final int TYPE_FOOT = 2;

    private Context mContext;
    private List<RefreshBody> mBodyList;


    public RefreshAdapter(Context context, List<RefreshBody> bodies) {
        mContext = context;
        mBodyList = bodies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder mHolder = null;
        if (viewType == TYPE_BODY) {
            mHolder = new BodyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.refresh_recyclerview_item_body, parent, false));
        } else if (viewType == TYPE_FOOT) {
            mHolder = new FootViewHolder(LayoutInflater.from(mContext).inflate(R.layout.refresh_recyclerview_item_foot, parent, false));
        }
        return mHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BodyViewHolder) {
            final RefreshBody body = mBodyList.get(position);
            BodyViewHolder bodyViewHolder = (BodyViewHolder) holder;
            bodyViewHolder.bodyTv.setText(body.name + "  " + body.age);
            bodyViewHolder.bodyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, String.valueOf(body.age), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (holder instanceof FootViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return mBodyList.size() + 1;    //加上底部的加载更多
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = -1;
        if (position == mBodyList.size()) {
            viewType = TYPE_FOOT;
        } else {
            viewType = TYPE_BODY;
        }
        return viewType;
    }

    class BodyViewHolder extends RecyclerView.ViewHolder {

        private TextView bodyTv;

        public BodyViewHolder(View itemView) {
            super(itemView);
            bodyTv = itemView.findViewById(R.id.tv_body);

        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {


        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }
}
