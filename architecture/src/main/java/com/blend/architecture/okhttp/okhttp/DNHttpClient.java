package com.blend.architecture.okhttp.okhttp;



import com.blend.architecture.okhttp.okhttp.chain.Interceptor;

import java.util.ArrayList;
import java.util.List;


public class DNHttpClient {
    private Dispatcher dispatcher;
    private ConnectionPool connectionPool;
    private int retrys;
    private List<Interceptor> interceptors;

    public DNHttpClient() {
        this(new Builder());
    }

    public DNHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
        connectionPool = builder.connectionPool;
        retrys = builder.retrys;
        interceptors = builder.interceptors;
    }

    public Call newCall(Request request) {
        return new Call(this, request);
    }
    public int retrys() {
        return retrys;
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    public List<Interceptor> interceptors() {
        return interceptors;
    }



    public static final class Builder {
        /**
         * 队列 任务分发
         */
        Dispatcher dispatcher = new Dispatcher();
        ConnectionPool connectionPool = new ConnectionPool();
        //默认重试3次
        int retrys = 3;
        List<Interceptor> interceptors = new ArrayList<>();

        public Builder retrys(int retrys) {
            this.retrys = retrys;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }
    }
}
