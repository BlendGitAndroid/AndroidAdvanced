package com.blend.architecture.glide.glide.load.model.data;

import android.net.Uri;

import com.blend.architecture.glide.glide.load.model.data.DataFetcher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUriFetcher implements DataFetcher<InputStream> {

    private final Uri uri;
    private boolean isCancelled;

    public HttpUriFetcher(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void loadData(DataFetcherCallback<? super InputStream> callback) {
        HttpURLConnection connection = null;
        InputStream stream = null;
        try {
            connection = (HttpURLConnection) new URL(uri.toString())
                    .openConnection();
            connection.connect();
            stream = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            if (isCancelled) {
                return;
            }
            if (responseCode == 200) {
                callback.onFetcherReady(stream);
            } else {
                callback.onLoadFailed(new RuntimeException(connection.getResponseMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onLoadFailed(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    @Override
    public void cancel() {
        isCancelled = true;
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
