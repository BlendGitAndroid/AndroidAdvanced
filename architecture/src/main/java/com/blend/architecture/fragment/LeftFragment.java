package com.blend.architecture.fragment;


import android.content.Context;
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

import com.blend.architecture.R;

public class LeftFragment extends Fragment {

    private static final String TAG = "LeftFragment";

    private Button mLeft;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach() called with: context = [" + context + "]");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left_layout, container, false);
        mLeft = view.findViewById(R.id.leftBtn);
        Log.d(TAG, "onCreateView: ");
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " + getActivity());
                Log.e(TAG, "onClick fragment: " + LeftFragment.this.hashCode());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: " + getActivity());
                        Log.e(TAG, "run fragment: " + LeftFragment.this.a());
                    }
                }, 5000);
            }
        });
        return view;
    }

    //在上面这里，getActivity为null，是因为上一个Activity已经销毁了，而fragment保存的还是上一个activity的引用，所以会为null
    //但是下面的Fragment.this.a()能正常打印，是因为发生了内存泄露，MeaageQueue持有了Fragment的引用
    //但是为什么getActivity会为null呢，是因为Activity持有HostCallbacks，而HostCallbacks也持有Activity，他们两个相互持有，
    //Fragment持有mHost，但是这个mHost在Detach的时候被置为了null，也就是fragment不持有Activity的引用了，那么Activity就能
    //正常的释放了。
    public String a() {
        Log.e(TAG, "aaaa: ");
        return "a";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView() called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }
}
