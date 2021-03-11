package com.blend.ui.judge_nested_scroll;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blend.ui.R;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        mRecyclerView = findViewById(R.id.coorRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerAdapter(this, getDatalist()));
    }

    private List<Integer> getDatalist() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        return list;
    }

    static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<Integer> mIntegerList = new ArrayList<>();
        private Context mContext;

        public RecyclerAdapter(Context context, List<Integer> integerList) {
            mIntegerList = integerList;
            mContext = context;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coordinator_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.mTextView.setText("position " + position);
            if (position % 2 == 0) {
                holder.mTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange));
            } else {
                holder.mTextView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.splash_bg));
            }
        }

        @Override
        public int getItemCount() {
            return mIntegerList.size();
        }
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.coordinatorText);
        }
    }
}