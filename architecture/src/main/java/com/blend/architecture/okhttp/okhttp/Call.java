package com.blend.architecture.okhttp.okhttp;


import com.blend.architecture.okhttp.okhttp.chain.CallServiceInterceptor;
import com.blend.architecture.okhttp.okhttp.chain.ConnectionInterceptor;
import com.blend.architecture.okhttp.okhttp.chain.HeadersInterceptor;
import com.blend.architecture.okhttp.okhttp.chain.Interceptor;
import com.blend.architecture.okhttp.okhttp.chain.InterceptorChain;
import com.blend.architecture.okhttp.okhttp.chain.RetryInterceptor;

import java.io.IOException;
import java.util.ArrayList;

public class Call {


    Request request;
    DNHttpClient client;


    public Request request() {
        return request;
    }

    public DNHttpClient client() {
        return client;
    }

    /**
     * 是否执行过
     */
    boolean executed;

    boolean canceled;

    public Call(DNHttpClient client, Request request) {
        this.client = client;
        this.request = request;
    }

    public Call enqueue(Callback callback) {
        //不能重复执行
        synchronized (this) {
            if (executed) {
                throw new IllegalStateException("Already Executed");
            }
            executed = true;
        }
        client.dispatcher().enqueue(new AsyncCall(callback));
        return this;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    Response getResponse() throws IOException {
        ArrayList<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
        interceptors.add(new RetryInterceptor());
        interceptors.add(new HeadersInterceptor());
        interceptors.add(new ConnectionInterceptor());
        interceptors.add(new CallServiceInterceptor());
        InterceptorChain interceptorChain = new InterceptorChain(interceptors, 0, this, null);
        return interceptorChain.proceed();
    }

    final class AsyncCall implements Runnable {

        private final Callback callback;

        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            //是否已经通知过callback
            boolean signalledCallback = false;
            try {
                Response response = getResponse();
                if (canceled) {
                    signalledCallback = true;
                    callback.onFailure(Call.this, new IOException("Canceled"));
                } else {
                    signalledCallback = true;
                    callback.onResponse(Call.this, response);
                }
            } catch (IOException e) {
                if (!signalledCallback) {
                    callback.onFailure(Call.this, e);
                }
            } finally {
                client.dispatcher().finished(this);
            }
        }

        public String host() {
            return request.url().host;
        }
    }


}
