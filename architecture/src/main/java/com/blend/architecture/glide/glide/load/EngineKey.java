package com.blend.architecture.glide.glide.load;




import com.blend.architecture.glide.glide.cache.Key;

import java.security.MessageDigest;

public class EngineKey implements Key {

    private final Object model;
    private final int width;
    private final int height;
    private int hashCode;

    public EngineKey(Object model, int width, int height) {
        this.model = model;
        this.width = width;
        this.height = height;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(getKeyBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return toString().getBytes();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        EngineKey engineKey = (EngineKey) object;

        if (width != engineKey.width) return false;
        if (height != engineKey.height) return false;
        if (hashCode != engineKey.hashCode) return false;
        return model != null ? model.equals(engineKey.model) : engineKey.model == null;
    }

    @Override
    public int hashCode() {
        int result = model != null ? model.hashCode() : 0;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + hashCode;
        return result;
    }
//    @Override
//    public boolean equals(Object object) {
//        if (this == object) return true;
//        if (object == null || getClass() != object.getClass()) return false;
//
//        EngineKey engineKey = (EngineKey) object;
//
//        if (width != engineKey.width) return false;
//        if (height != engineKey.height) return false;
//        if (hashCode != engineKey.hashCode) return false;
//        return model != null ? model.equals(engineKey.model) : engineKey.model == null;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = model != null ? model.hashCode() : 0;
//        result = 31 * result + width;
//        result = 31 * result + height;
//        result = 31 * result + hashCode;
//        return result;
//    }

    @Override
    public String toString() {
        return "EngineKey{" +
                "model=" + model +
                ", width=" + width +
                ", height=" + height +
                ", hashCode=" + hashCode +
                '}';
    }
}
