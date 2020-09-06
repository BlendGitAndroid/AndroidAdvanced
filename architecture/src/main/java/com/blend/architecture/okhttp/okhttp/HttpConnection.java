package com.blend.architecture.okhttp.okhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

public class HttpConnection {

    Socket socket;
    InputStream is;
    OutputStream os;

    long lastUsetime;

    Request request;

    static final String HTTPS = "https";


    public void setRequest(Request request) {
        this.request = request;
    }


    public boolean isSameAddress(String host, int port) {
        if (null == socket) {
            return false;
        }
        return TextUtils.equals(socket.getInetAddress().getHostName(), host) && port == socket
                .getPort();
    }

    /**
     * 创建socket
     *
     * @throws IOException
     */
    private void createSocket() throws IOException {
        if (null == socket || socket.isClosed()) {
            HttpUrl url = request.url();
            //需要sslsocket
            if (url.protocol.equalsIgnoreCase(HTTPS)) {
                socket = SSLSocketFactory.getDefault().createSocket();
            } else {
                socket = new Socket();
            }
            socket.connect(new InetSocketAddress(url.host, url.port));
            os = socket.getOutputStream();
            is = socket.getInputStream();
        }
    }

    public InputStream call(HttpCodec httpCodec) throws IOException {
        try {
            createSocket();
            //写出请求
            httpCodec.writeRequest(os, request);
            return is;
        } catch (Exception e) {
            closeQuietly();
            throw new IOException(e);
        }
    }


    public void closeQuietly() {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void updateLastUseTime() {
        //更新最后使用时间
        lastUsetime = System.currentTimeMillis();
    }
}
