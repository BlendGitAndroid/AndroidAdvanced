package com.blend.optimization.memory;

import java.lang.ref.WeakReference;

public class MemorySingleton {

    private static MemorySingleton singleton;

    //    private Callback callback;
    private WeakReference<Callback> callback;

    public static MemorySingleton getInstance() {
        if (singleton == null) {
            singleton = new MemorySingleton();
        }
        return singleton;
    }


    public void setCallback(Callback callback) {
        this.callback = new WeakReference<>(callback);
    }

    public Callback getCallback() {
        return callback.get();
    }

    public interface Callback {
        void callback();
    }

}
