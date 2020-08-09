package com.example.pluginstand;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public interface PayInterfaceActivity {
    public void attach(Activity proxyActivity);

    /**
     *生命周期
     */
    public void onCreate(Bundle saveInstanceState);
    public void onStart();
    public void onResume();
    public void onPause();
    public void onStop();
    public void onDestroy();
    public void onSaveInstanceState(Bundle outState);
    public boolean onTouchEvent(MotionEvent event);
    public void onBackPressed();

}
