package com.blend.architecture.eventbus.core;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectCenter {

    private static final String TAG = "ObjectCenter";

    private static volatile ObjectCenter sInstance = null;

    private final ConcurrentHashMap<String, Object> mObjects;

    private ObjectCenter() {
        mObjects = new ConcurrentHashMap<String, Object>();
    }

    public static ObjectCenter getInstance() {
        if (sInstance == null) {
            synchronized (ObjectCenter.class) {
                if (sInstance == null) {
                    sInstance = new ObjectCenter();
                }
            }
        }
        return sInstance;
    }

    public Object getObject(String name) {
        return mObjects.get(name);
    }

    public void putObject(String name, Object object) {
        mObjects.put(name, object);
    }

}
