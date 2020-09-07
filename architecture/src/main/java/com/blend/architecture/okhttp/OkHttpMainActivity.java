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
 * 设计模式：建造者模式，责任链模式，克隆模式(Call接口)
 * <p>
 * 在网络的请求和返回的过程中，对每一步的过程如拼接请求头，发射，连接等都封装成一个对象，目的是单一职责，为了更好的扩展。
 * <p>
 * 网络请求发送的流程：
 * 1.dns解析，域名对应 ip
 * 2.TCP建立连接,三次握手
 * 3.C端向S端发送请求行命令
 * 4.C端发送请求头信息
 * 5.S端应答，发送响应命令
 * 6.S端发送响应头信息
 * 7.S端向C端发送数据，以及消息体
 * 8.S端关闭链接 tcp 四次挥手
 * <p>
 * okHttp的异步请求只是比同步请求增加了线程调度的功能，我们先来看下异步与同步请求不同的地方：
 * 当我们调用call.enqueue(Callback)时，就会发起一个异步请求，实际执行的是realCall.enqueue(Callback)，它比同步请求只是
 * 多了一个Callback参数，这个Callback就是用来进行回调，然后realCall.execute()中先把传进来的Callback包装成一个AsyncCall，
 * 然后执行Dispatcher的enqueue(AsyncCall)把这个异步请求任务保存进readyAsyncCalls队列中，保存后开始执行promoteAndExecute()
 * 进行异步任务的调度，它会先把符合条件的异步请求任务从readyAsyncCalls转移到runningAsyncCalls队列和添加到executableCalls列
 * 表中去，然后遍历executableCalls列表，逐个执行AsyncCall的executeOn(ExecutorService)，然后在这个方法中AsyncCall会把自己
 * 放进Dispatcher的线程池，等待线程池的调度。当线程池执行到这个AsyncCall时，它的run方法就会被执行，从而执行重写的execute()方法，
 * execute()方法中的流程和同步请求流程大致相同。
 * <p>
 * okHttp的整个流程：
 * 1.okhttp通过Builder模式创建OkHttpClient、Request和Response，
 * 2.通过client.newCall(Resquest)创建一个Call，用于发起异步或同步请求
 * 3.请求会经过Dispatcher、一系列拦截器，最后通过okio与服务器建立连接、发送数据并解析返回结果。
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
        OkHttpClient builder = new OkHttpClient().newBuilder().build(); //利用建造者模式，用于添加自定义属性

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