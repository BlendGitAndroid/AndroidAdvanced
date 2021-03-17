package com.blend.ui.judge_nested_scroll;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blend.ui.R;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "ViewPagerAdapter";

    private List<Integer> mViews;

    public ViewPagerAdapter(List<Integer> list) {
        mViews = list;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view1 = LayoutInflater.from(container.getContext()).inflate(R.layout.view_pager1_layout, container, false);
        ImageView imageView = view1.findViewById(R.id.viewPagerIv);
        imageView.setImageResource(mViews.get(position));
        container.addView(view1);
        Log.e(TAG, "instantiateItem: " + position + "---" + view1);
        return view1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.e(TAG, "destroyItem: " + position + "---" + object);
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "苹果";
        } else if (position == 1) {
            return "橘子";
        } else {
            return "梨";
        }
    }
}
