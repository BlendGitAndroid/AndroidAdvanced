package com.blend.architecture.glide.glide.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.blend.architecture.glide.glide.RequestManager;


public class RequestManagerRetriever {
    static final String FRAGMENT_TAG = "com.bumptech.glide.manager";

    private volatile RequestManager applicationManager;

    private RequestManager newManager(Lifecycle lifecycle, Context context) {
        return new RequestManager(lifecycle, context);
    }

    private RequestManager getApplicationManager(Context context) {
        if (applicationManager == null) {
            synchronized (this) {
                if (applicationManager == null) {
                    applicationManager = newManager(new ApplicationLifecycle(),
                            context.getApplicationContext());
                }
            }
        }

        return applicationManager;
    }

    /**
     * 根据参数配置不同生命周期
     * 如Activity，则创建一个android Fragment，则可以通过Fragment同步生命周期
     *
     * @param context
     * @return
     */
    public RequestManager get(Context context) {
        if (!(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return get((FragmentActivity) context);
            } else if (context instanceof Activity) {
                return get((Activity) context);
            } else if (context instanceof ContextWrapper) {
                return get(((ContextWrapper) context).getBaseContext());
            }
        }
        return getApplicationManager(context);
    }

    public RequestManager get(View view) {
        return get(view.getContext());
    }


    public RequestManager get(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        return supportFragmentGet(activity, fm);
    }


    public RequestManager get(Fragment fragment) {
        FragmentManager fm = fragment.getChildFragmentManager();
        return supportFragmentGet(fragment.getActivity(), fm);
    }


    public RequestManager get(Activity activity) {
        android.app.FragmentManager fm = activity.getFragmentManager();
        return fragmentGet(activity, fm);
    }


    public RequestManager get(android.app.Fragment fragment) {
        android.app.FragmentManager fm = fragment.getChildFragmentManager();
        return fragmentGet(fragment.getActivity(), fm);
    }


    RequestManagerFragment getRequestManagerFragment(
            final android.app.FragmentManager fm) {
        RequestManagerFragment current = (RequestManagerFragment) fm.findFragmentByTag
                (FRAGMENT_TAG);
        if (current == null) {
            current = new RequestManagerFragment();
            fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        return current;
    }


    private RequestManager fragmentGet(Context context,
                                       android.app.FragmentManager fm) {
        RequestManagerFragment current = getRequestManagerFragment(fm);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            requestManager = newManager(current.getGlideLifecycle(), context);
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }


    SupportRequestManagerFragment getSupportRequestManagerFragment(
            final FragmentManager fm) {
        SupportRequestManagerFragment current =
                (SupportRequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = new SupportRequestManagerFragment();
            fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        return current;
    }


    private RequestManager supportFragmentGet(Context context, FragmentManager fm) {
        SupportRequestManagerFragment current = getSupportRequestManagerFragment(fm);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            requestManager = newManager(current.getGlideLifecycle(), context);
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }

}
