package com.blend.architecture.glide.glide.load.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;


import com.blend.architecture.glide.glide.load.ObjectKey;
import com.blend.architecture.glide.glide.load.model.data.FileFetcher;

import java.io.InputStream;

public class UriFileLoader implements ModelLoader<Uri, InputStream> {
    private final ContentResolver contentResolver;


    public UriFileLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }


    @Override
    public LoadData<InputStream> buildLoadData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new FileFetcher(uri, contentResolver));
    }

    @Override
    public boolean handles(Uri uri) {
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {
        private final ContentResolver contentResolver;

        public Factory(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @NonNull
        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry modelLoaderRegistry) {
            return new UriFileLoader(contentResolver);
        }

    }

}
