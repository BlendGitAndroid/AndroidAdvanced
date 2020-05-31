package com.blend.ui.custom_recycler;

import android.view.View;
import android.view.ViewGroup;

public interface Adapter {

    int getCount();

    //Item类型
    int getItemViewType(int row);

    //Item的类型数量
    int getViewTypeCount();

    View getView(int position, View convertView, ViewGroup parent);

    int getHeight(int index);

}
