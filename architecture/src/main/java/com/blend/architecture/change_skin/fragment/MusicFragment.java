package com.blend.architecture.change_skin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blend.architecture.R;
import com.blend.architecture.change_skin.adapter.GirlAdapter;


public class MusicFragment extends Fragment {
    private View mView;
    private RecyclerView mRelView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_music, container, false);
        mRelView = (RecyclerView) mView.findViewById(R.id.rel_view);
        //设置布局管理器
        mRelView.setLayoutManager(new LinearLayoutManager(getContext()));
        GirlAdapter girlAdapter = new GirlAdapter();
        mRelView.setAdapter(girlAdapter);
        return mView;
    }

    @Override
    public LayoutInflater onGetLayoutInflater(Bundle savedInstanceState) {
        return super.onGetLayoutInflater(savedInstanceState);
    }
}


