package com.blend.architecture.okhttp.okhttp;

public interface Callback {
    void onFailure(Call call, Throwable throwable);

    void onResponse(Call call, Response response);
}
