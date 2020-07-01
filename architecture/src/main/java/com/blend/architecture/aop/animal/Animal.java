package com.blend.architecture.aop.animal;

import android.util.Log;

public class Animal {

    private static final String TAG = "Animal";

    public void fly() {
        Log.e(TAG, "Animal fly method: " + this.toString());
    }

}
