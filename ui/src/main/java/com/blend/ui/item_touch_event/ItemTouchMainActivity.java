package com.blend.ui.item_touch_event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blend.ui.R;
import com.blend.ui.item_touch_event.item_touch_helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

//参考：https://www.jianshu.com/p/130fdd755471

//bug，点击refresh有时候不刷新

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

        /**
         *
         * 一些小的总结：
         * 1.swipe和drag模式进入的时机。swipe模式进入的判断是在OnItemTouchListener帮助类里面onTouchEvent()的函数
         * 的checkSelectForSwipe()的调用里面判断是否进入，drag模式进入的判断是在GestureDetectorCompat帮助里
         * ItemTouchHelperGestureListener里面onLongPress()里面判断是否进入。
         *
         * 2.触摸事件在移动的过程中(信息信息请看OnItemTouchListener帮助类里面onTouchEvent()函数的MotionEvent.ACTION_MOVE逻辑处理)
         * 会一直去更新滑动的位置(updateDxDy函数)和一直让去重绘(mRecyclerView.invalidate的调用)。
         *
         */
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
