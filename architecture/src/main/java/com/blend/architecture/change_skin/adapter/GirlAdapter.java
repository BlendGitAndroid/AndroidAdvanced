package com.blend.architecture.change_skin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blend.architecture.R;


public class GirlAdapter extends RecyclerView.Adapter<GirlAdapter.MyViewHolder> {

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_adapter, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.name.setText(position + "==blend");
    }

    @Override
    public int getItemCount()
    {
        return 10;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}
