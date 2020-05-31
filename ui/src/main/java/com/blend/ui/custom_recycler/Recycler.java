package com.blend.ui.custom_recycler;

import android.view.View;

import java.util.Stack;

public class Recycler {

    //打造一个回收池
    private Stack<View>[] views;

    //实例化一个栈数组
    public Recycler(int typeNumber) {
        views = new Stack[typeNumber];
        for (int i = 0; i < typeNumber; i++) {
            views[i] = new Stack<>();
        }
    }

    public void addRecyclerView(View view, int type) {
        views[type].push(view);
    }

    //只关心取到View对应的类型
    public View getRecyclerView(int type) {
        try {
            return views[type].pop();
        } catch (Exception e) {
            return null;
        }
    }

}
