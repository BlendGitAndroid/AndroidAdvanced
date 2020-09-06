package com.blend.architecture.okhttp.okhttp;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpUrl {


    String protocol;	//协议：http.https
    String host;   //192.168.2.3
    String file;	//地址
    int port;	//端口

    /**
     * scheme://host:port/path?query#fragment
     * @param url
     * @throws MalformedURLException
     */
    public HttpUrl(String url) throws MalformedURLException {
        URL url1 = new URL(url);
        host = url1.getHost();
        file = url1.getFile();
        file = TextUtils.isEmpty(file) ? "/" : file;
        protocol = url1.getProtocol();
        port = url1.getPort();
        port = port == -1 ? url1.getDefaultPort() : port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }
}
