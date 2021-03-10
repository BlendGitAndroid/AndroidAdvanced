package com.blend.ui.judge_nested_scroll;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blend.ui.R;

import java.util.ArrayList;
import java.util.List;

public class PictureFragment extends Fragment {

    private RecyclerView mRecyclerView;

    public static PictureFragment getInstance() {
        return new PictureFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new PictureAdapter(getList()));
        return view;
    }

    private List<PictureBean> getList() {
        List<PictureBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PictureBean bean = new PictureBean();
            bean.picture = R.drawable.pear;
            bean.content = "故事讲述了从前有个人见人爱的小姑娘，喜欢戴着外婆送给她的一顶红色天鹅绒的帽子，于是大家就叫她小红帽。有一天，母亲叫她给住在森林的外婆送食物，并嘱咐她不要离开大路，走得太远。";
            list.add(bean);
        }
        return list;
    }

}
