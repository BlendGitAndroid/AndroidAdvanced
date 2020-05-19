package com.blend.ui.recyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.blend.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RefreshRecyclerViewActivity extends AppCompatActivity {

    private PullRefreshRecyclerView mRefreshRecyclerView;
    private List<RefreshBody> mRefreshBodies = new ArrayList<>();
    private Handler mHandler = new Handler();

    private int pullIndex = 100;
    private int moreIndex = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        mRefreshRecyclerView = findViewById(R.id.pullRefreshRecyclerView);
        mRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        RefreshAdapter adapter = new RefreshAdapter(this, mRefreshBodies);
        mRefreshRecyclerView.setAdapter(adapter);

        mRefreshRecyclerView.setOnPullListener(new PullRefreshRecyclerView.OnPullListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshBodies.add(0, new RefreshBody("新数据", pullIndex++));
                        mRefreshRecyclerView.refreshFinish();
                    }
                }, 3000);
            }

            @Override
            public void onLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshBodies.add(new RefreshBody("加载更多", moreIndex--));
                        mRefreshRecyclerView.loadMoreFinish();
                    }
                }, 3000);
            }
        });

    }

    private void initData() {
        for (int j = 0; j < 10; j++) {
            mRefreshBodies.add(new RefreshBody("原始数据", j));
        }
    }


}
