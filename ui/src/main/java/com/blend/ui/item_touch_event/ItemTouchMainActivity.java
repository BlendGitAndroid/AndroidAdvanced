package com.blend.ui.item_touch_event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blend.ui.R;
import com.blend.ui.item_touch_event.item_touch_helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemTouchMainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MainRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_touch_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MainRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        mAdapter.updateData(createTestDatas());

        ItemTouchHelpCallback callback = new ItemTouchHelpCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private List<ItemTouchModel> createTestDatas() {
        List<ItemTouchModel> result = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ItemTouchModel testModel = new ItemTouchModel(i, ":Item Swipe Action Button Container Width");
            if (i == 1) {
                testModel = new ItemTouchModel(i, "Item Swipe with Action container width and no spring");
            }
            if (i == 2) {
                testModel = new ItemTouchModel(i, "Item Swipe with RecyclerView Width");
            }
            result.add(testModel);
        }
        return result;
    }
}
