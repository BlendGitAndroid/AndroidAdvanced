package com.blend.architecture.okhttp.okhttp.chain;

import android.util.Log;

import com.blend.architecture.okhttp.okhttp.HttpCodec;
import com.blend.architecture.okhttp.okhttp.HttpConnection;
import com.blend.architecture.okhttp.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class CallServiceInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.e("interceprot", "通信拦截器....");
        HttpCodec httpCodec = chain.httpCodec;
        HttpConnection connection = chain.connection;
        InputStream is = connection.call(httpCodec);
        //HTTP/1.1 200 OK 空格隔开的响应状态
        String statusLine = httpCodec.readLine(is);

        Map<String, String> headers = httpCodec.readHeaders(is);
        //是否保持连接
        boolean isKeepAlive = false;
        if (headers.containsKey(HttpCodec.HEAD_CONNECTION)) {
            isKeepAlive = headers.get(HttpCodec.HEAD_CONNECTION).equalsIgnoreCase(HttpCodec
                    .HEAD_VALUE_KEEP_ALIVE);
        }
        int contentLength = -1;
        if (headers.containsKey(HttpCodec.HEAD_CONTENT_LENGTH)) {
            contentLength = Integer.valueOf(headers.get(HttpCodec.HEAD_CONTENT_LENGTH));
        }
        //分块编码数据
        boolean isChunked = false;
        if (headers.containsKey(HttpCodec.HEAD_TRANSFER_ENCODING)) {
            isChunked = headers.get(HttpCodec.HEAD_TRANSFER_ENCODING).equalsIgnoreCase(HttpCodec
                    .HEAD_VALUE_CHUNKED);
        }
        String body = null;
        if (contentLength > 0) {
            byte[] bytes = httpCodec.readBytes(is, contentLength);
            body = new String(bytes);
        } else if (isChunked) {
            body = httpCodec.readChunked(is);
        }
        String[] status = statusLine.split(" ");
        connection.updateLastUseTime();
        return new Response(Integer.valueOf(status[1]), contentLength, headers, body, isKeepAlive);
    }
}
