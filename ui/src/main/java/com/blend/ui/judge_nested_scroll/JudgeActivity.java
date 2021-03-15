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
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), getFragments()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ArticleFragment.getInstance());
        fragments.add(PictureFragment.getInstance());
        fragments.add(ArticleFragment.getInstance());
        return fragments;
    }

}