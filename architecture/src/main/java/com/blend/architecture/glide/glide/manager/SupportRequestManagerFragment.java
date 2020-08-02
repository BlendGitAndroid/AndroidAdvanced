package com.blend.architecture.glide.glide.manager;

import android.support.v4.app.Fragment;

import com.blend.architecture.glide.glide.RequestManager;


public class SupportRequestManagerFragment extends Fragment {

    /**
     * 生命周期回调管理类
     */
    private final ActivityFragmentLifecycle lifecycle;


    private RequestManager requestManager;

    public SupportRequestManagerFragment() {
        this.lifecycle = new ActivityFragmentLifecycle();
    }

    /**
     * 当前的请求管理类
     *
     * @param requestManager
     */
    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }


    ActivityFragmentLifecycle getGlideLifecycle() {
        return lifecycle;
    }


    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }

}
