package com.blend.architecture.glide.glide;

import android.content.Context;
import android.widget.ImageView;


import com.blend.architecture.glide.glide.request.Request;
import com.blend.architecture.glide.glide.request.RequestOptions;

import java.io.File;


/**
 * A generic class that can handle setting options and staring loads for generic resource types.
 */
public class RequestBuilder implements
        ModelTypes<RequestBuilder> {


    private final Context context;
    private final RequestManager requestManager;
    private final RequestOptions defaultRequestOptions;

    protected RequestOptions requestOptions;

    private Object model;

    protected RequestBuilder(RequestManager requestManager, Context context) {
        this.requestManager = requestManager;
        this.defaultRequestOptions = Glide.get(context).getDefaultRequestOptions();
        this.context = context;
        //自己的options 默认的不能修改
        this.requestOptions = defaultRequestOptions;
    }

    public RequestBuilder apply(RequestOptions requestOptions) {
        this.requestOptions = requestOptions;
        return this;
    }


    private RequestBuilder loadGeneric(Object model) {
        this.model = model;
        return this;
    }


    @Override
    public RequestBuilder load(String string) {
        return loadGeneric(string);
    }


    @Override
    public RequestBuilder load(File file) {
        return loadGeneric(file);
    }


    public Target into(ImageView view) {
        Target target = new Target(view);
        Request request = new Request(context, model, requestOptions, target);
        target.setRequest(request);
        //追踪并开始执行请求
        requestManager.track(request);
        return target;
    }

}
