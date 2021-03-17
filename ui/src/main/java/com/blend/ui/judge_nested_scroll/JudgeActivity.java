package com.blend.ui.judge_nested_scroll;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.blend.ui.R;

import java.util.ArrayList;
import java.util.List;

public class JudgeActivity extends AppCompatActivity {

    private static final String TAG = "JudgeActivity";

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ui);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        //FragmentPagerAdapter
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), getFragments()));
        //PagerAdapter
        // mViewPager.setAdapter(new ViewPagerAdapter(getViewList()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private List<Integer> getViewList() {
        List<Integer> views = new ArrayList<>();
        views.add(R.drawable.apple);
        views.add(R.drawable.orange);
        views.add(R.drawable.pear);
        return views;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ArticleFragment.getInstance().setData("0"));
        fragments.add(ArticleFragment.getInstance().setData("1"));
        fragments.add(ArticleFragment.getInstance().setData("2"));
        fragments.add(ArticleFragment.getInstance().setData("3"));
        fragments.add(ArticleFragment.getInstance().setData("4"));
        fragments.add(ArticleFragment.getInstance().setData("5"));
        return fragments;
    }

}