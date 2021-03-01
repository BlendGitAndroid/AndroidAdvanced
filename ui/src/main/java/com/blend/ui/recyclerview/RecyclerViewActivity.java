package com.blend.ui.recyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.blend.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView rv;
    private int[] iv = new int[]{
            R.drawable.duo,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view2);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this, 1));

        final List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            list.add(iv[0]);
        }
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, list);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> Toast.makeText(RecyclerViewActivity.this, "第几个：" + position, Toast.LENGTH_SHORT).show());
    }
}