package com.blend.architecture.glide.glide.load;



import com.blend.architecture.glide.glide.cache.Key;

import java.security.MessageDigest;

public class ObjectKey implements Key {


    private final Object object;

    public ObjectKey(Object object) {
        this.object = object;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(object.toString().getBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return new byte[0];
    }

    @Override
    public boolean equals(Object object1) {
        if (this == object1) {
            return true;
        }
        if (object1 == null || getClass() != object1.getClass()) {
            return false;
        }
        ObjectKey objectKey = (ObjectKey) object1;

        return object != null ? object.equals(objectKey.object) : objectKey.object == null;
    }

    @Override
    public int hashCode() {
        return object != null ? object.hashCode() : 0;
    }
}
