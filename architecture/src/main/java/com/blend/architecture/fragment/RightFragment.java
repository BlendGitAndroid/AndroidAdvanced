package com.blend.architecture.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blend.architecture.R;

public class RightFragment extends Fragment {

    private Button mRight;
    private TextView mRightTv;
    private Handler mHandler;
    private String mName;

    public static Fragment newInstance(Handler handler, String name) {
        RightFragment fragment = new RightFragment();
        fragment.setHandle(handler);
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = getArguments().getString("name");
    }

    private void setHandle(Handler handler) {
        mHandler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right_layout, container, false);
        mRight = view.findViewById(R.id.rightBtn);
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bug1();
            }
        });
        mRightTv = view.findViewById(R.id.rightTv);
        mRightTv.setText(mName);
        return view;

    }

    private void bug1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }
}
