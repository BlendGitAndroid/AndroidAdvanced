package com.blend.architecture.okhttp.okhttp.chain;


import com.blend.architecture.okhttp.okhttp.Response;

import java.io.IOException;

public interface Interceptor {

    Response intercept(InterceptorChain chain) throws IOException;
}
