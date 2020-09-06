package com.blend.architecture.okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blend.architecture.R;
import com.blend.architecture.okhttp.okhttp.Call;
import com.blend.architecture.okhttp.okhttp.Callback;
import com.blend.architecture.okhttp.okhttp.DNHttpClient;
import com.blend.architecture.okhttp.okhttp.Request;
import com.blend.architecture.okhttp.okhttp.RequestBody;
import com.blend.architecture.okhttp.okhttp.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import okhttp3.OkHttpClient;


/**
 * 设计模式：建造者模式，责任链模式
 * <p>
 * 在网络的请求和返回的过程中，对每一步的过程如拼接请求头，发射，连接等都封装成一个对象，目的是单一职责，为了更好的扩展
 */
public class OkHttpMainActivity extends AppCompatActivity {

    private static final String TAG = "OkHttpMainActivity";

    private DNHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http_main);
        test();

        client = new DNHttpClient();
        getCustomizeTest();
        postCustomizeTest();
    }

    private void postCustomizeTest() {
        RequestBody body = new RequestBody()
                .add("city", "长沙")
                .add("key", "13cb58f5884f9749287abbead9c658f2");
        Request request = new Request.Builder().url("http://restapi.amap" +
                ".com/v3/weather/weatherInfo").post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.e(TAG, "post响应体: " + response.getBody());
            }
        });
    }

    private void getCustomizeTest() {
        Request request = new Request.Builder()
                .url("http://www.kuaidi100.com/query?type=yuantong&postid=11111111111")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.e(TAG, "get响应体: " + response.getBody());
            }
        });
    }

    private void test() {
        String url = "http://www.baidu.com";
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()  //默认为GET请求，可以不写
                .build();
        final okhttp3.Call call = client.newCall(request);
        try {
            call.execute();//同步
            call.enqueue(new okhttp3.Callback() {   //异步
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                }
            });
            call.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket socket = null;
        try {
//             socket = SSLSocketFactory.getDefault().createSocket("wwww.baidu.com", 443);
            socket = new Socket("wwww.baidu.com/adb.js", 80);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(("GET / HTTP/1.1\r\n" +
                    "Host: wwww.baidu.com\r\n\r\n").getBytes());
            outputStream.flush();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = socket.getInputStream().read(bytes)) != -1) {
                String s = new String(bytes, 0, len);
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        OkHttpClient client1 = new OkHttpClient().newBuilder().addInterceptor();  // 应用拦截器
//        OkHttpClient client2 = new OkHttpClient().newBuilder().addNetworkInterceptor();  // 网络拦截器
    }
}