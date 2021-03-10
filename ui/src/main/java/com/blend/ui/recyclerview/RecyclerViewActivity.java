package com.blend.ui.recyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blend.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private BaseRecyclerView rv;
    private Button clickRv;
    private int[] iv = new int[]{
            R.drawable.duo,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view2);

        rv = findViewById(R.id.rv);
        clickRv = findViewById(R.id.clickRv);
        rv.setLayoutManager(new GridLayoutManager(this, 1));

        final List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(iv[0]);
        }
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, list);
        rv.setAdapter(adapter);

        clickRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(2, R.drawable.avatar);
                adapter.notifyItemInserted(2);
                adapter.notifyItemRangeChanged(2, list.size() - 2);
            }
        });

        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(RecyclerViewActivity.this, "第几个：" + position, Toast.LENGTH_SHORT).show();
            }
        });

        rv.setOnItemClickListener(new BaseRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View item, int adapterPosition, RecyclerView.Adapter adapter) {
                Toast.makeText(RecyclerViewActivity.this, "adapterPosition:" + adapterPosition, Toast.LENGTH_SHORT).show();
            }
        });
    }
}