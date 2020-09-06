package com.blend.architecture.okhttp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class HttpsTest {

    public static void test(Context context) throws Exception {
        //获得ssl上下文
        SSLContext sslContext = SSLContext.getInstance("TLS");
        //信任证书管理器
        TrustManagerFactory trustManager = TrustManagerFactory.getInstance("X509");
        //证书
        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(context.getAssets().open("12306.bks"), "dongnao".toCharArray());

        trustManager.init(keyStore);
        sslContext.init(null, trustManager.getTrustManagers(), null);
        //ssl socket工厂创建socket
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) socketFactory.createSocket("www.12306.cn", 443);
        doHttps(socket);
    }

    static void doHttps(Socket socket) throws Exception {

        //接受数据的输入流
        final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //发送数据 输出流
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    String line = null;
                    try {
                        while ((line = br.readLine()) != null) {
                            System.out.println("recv :" + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        bw.write("GET / HTTP/1.1\r\n");
        bw.write("Host: www.12306.cn\r\n\r\n");
        bw.flush();
    }

}
