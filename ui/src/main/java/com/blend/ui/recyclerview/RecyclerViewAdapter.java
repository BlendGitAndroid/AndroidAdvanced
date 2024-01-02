package com.blend.ui.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.blend.ui.R;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {

    private OnItemClickListener onItemClickListener;
    private Context context;
    private List<Integer> list;

    private static final String TAG = "RecyclerViewAdapter";

    public RecyclerViewAdapter(Context context, List<Integer> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv, parent, false);
        Log.e(TAG, "onCreateViewHolder: ");
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.CustomViewHolder holder, int position) {
        holder.iv.setImageResource(list.get(position));
        // holder.bind(onItemClickListener);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "aaa" + position, Toast.LENGTH_SHORT).show();
                // list.remove(position);
                // notifyItemRemoved(position);
                // notifyItemRangeChanged(position, list.size() - position);
                Toast.makeText(context, "aaa" + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                list.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
        Log.e(TAG, "onBindViewHolder: " + position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;

        public CustomViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
        }

        public void bind(final OnItemClickListener onItemClickListener) {
            //'为ItemView设置点击事件'
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

    }


    //'注入接口'
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // 最致命的是，在onBindViewHolder()中设置点击监听器还会导致 bug，因为“快照机制”，作为参数传入onItemClick()的
    // 索引值是在调用onBindViewHolder()那一刻生成的快照，如果数据发生增删，但因为各种原因没有及时刷新对应位置的视图
    // （onBindViewHolder()没有被再次调用），此时发生的点击事件拿到的索引就是错的。
    //'定义点击回调'
    public static interface OnItemClickListener {
        void onItemClick(int position);
    }

}
