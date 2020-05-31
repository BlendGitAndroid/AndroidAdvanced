package com.blend.ui.custom_recycler;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blend.ui.R;

public class CustomRecyclerViewActivity extends AppCompatActivity {

    private CustomRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_recycler_view);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(new RecyclerViewAdapter(this, 2));
    }

    class RecyclerViewAdapter implements Adapter {

        private LayoutInflater mInflater;
        private int mHeight;
        private int mCount;

        public RecyclerViewAdapter(Context context, int count) {
            Resources resources = context.getResources();
            mHeight = resources.getDimensionPixelSize(R.dimen.dp_40);
            mInflater = LayoutInflater.from(context);
            mCount = count;
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public int getItemViewType(int row) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_custom_recycler_item_view, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.text1);
            textView.setText("第" + position + "行");
            return convertView;
        }

        @Override
        public int getHeight(int index) {
            return mHeight;
        }
    }
}
