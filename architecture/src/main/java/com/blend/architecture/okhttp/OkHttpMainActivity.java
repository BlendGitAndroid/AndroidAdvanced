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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;


/**
 * 设计模式：建造者模式，责任链模式，克隆模式(Call接口)
 * builder模式是，先赋值正确，再创建对象。set是先给予默认值，然后修改。
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
 * Http请求报文：
 * 请求行：请求方法 + 空格 + URL + 空格 + 协议版本 + 回车符 + 换行符 (如GET www.baidu.com HTTP/1.1  )
 * 请求头：头部字段名 + 冒号（:） + 值 + 回车符 + 换行符 (如Host: www.weather.com.cn  )
 * 请求正文：一般使用在POST方法中，GET方法不存在请求正文。POST方法适用于需要客户填写表单的场合。与请求数据相关的最常使用的
 * 请求头是Content-Type和Content-Length。
 * Http响应报文：
 * 状态行：协议版本 + 空格 + 状态码 + 空格 + 状态码描述 + 回车符 + 换行符 (如HTTP/1.1 200 OK)
 * 响应头：头部字段名 + 冒号（:） + 值 + 回车符 + 换行符 (如Server: openresty  )
 * 响应体：
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
 * 1.okhttp通过Builder模式创建OkHttpClient、Request和Response。OkHttpClient是okhttp中的大管家，它将具体的工作分发到各个子
 * 系统中去完成，它使用Builder模式配置网络请求的各种参数如超时、拦截器、分发器等；Request代表着一个HTTP请求，它封装了请求的具体消息，
 * 如url、header、body等，它和OkHttpClient一样都是使用Budiler模式来配置自己的参数；
 * 2.通过client.newCall(Resquest)创建一个Call，用于发起异步或同步请求。Call是一个接口，它的具体实现类是RealCall，Call中定义了
 * 一些enqueue(Callback)、execute()等关键方法。
 * 3.请求会经过Dispatcher、一系列拦截器，最后通过okio与服务器建立连接、发送数据并解析返回结果。Dispatcher是一个任务调度器，它负责
 * <p>
 * 同步请求：当我们调用call.execute()时，就会发起一个同步请求，而call的实现类是RealCall，所以实际执行的是realCall.execute()，
 * realCall.execute()中执行Dispatcher的executed(RealCall)把这个同步请求任务保存进runningSyncCalls队列中，（没有做事情，只是入队出队。）
 * 然后RealCall执行getResponseWithInterceptorChain()处理同步请求，请求经过层层拦截器后到达最后一个拦截器CallServerInterceptor，
 * 在这个拦截器中通过Exchange把请求发送到服务器，然后同样的通过Exchange获得服务器的响应，根据响应构造Response，然后返回，最后RealCall执行
 * Dispatcher的finished(RealCall)把之前暂时保存的同步请求任务从runningSyncCalls队列中移除。
 * <p>
 * 异步请求：当我们调用call.enqueue()时，先把异步请求添加到readyAsyncCalls队列中，接着执行promoteAndExecute方法，主要有两个for循环，第1个
 * for循环是把符合条件的异步请求任务从readyAsyncCalls转移（提升）到runningAsyncCalls队列和添加到executableCalls列表中去，第2个for循环就是
 * 遍历executableCalls列表，从executableCalls列表中获取AsyncCall对象，并且调用它的executeOn()方法，然后进行异步任务的执行。首先调用RealCall
 * 的getResponseWithInterceptorChain()方法处理请求请求处理完毕后，返回响应Response，这时回调我们调用Call.enqueue(Callback)时传进来的
 * Callback的onResponse()方法，最后在finally语句中调用Dispatcher的finished(AsyncCall)方法来把异步请求任务从runningAsyncCalls队列中移除出去。
 *
 * <p>
 * okHttp添加拦截器及具体的网络请求过程：
 * okHttp不管同步还是异步，最终都调用的是RealCall#getResponseWithInterceptorChain()方法，在这个方法中，依次添加到拦截器集合中，
 * 1.client.interceptors()使用者添加的应用拦截器：处理原始请求和最终的响应：可以添加自定义header、通用参数、参数加密、网关接入等等
 * 2.RetryAndFollowUpInterceptor(client)重试拦截器：处理错误重试和重定向。根据不同的错误状态码，生成新的Request请求，进行
 * 3.BridgeInterceptor(client.cookieJar())桥接拦截器：应用层和网络层的桥接拦截器，主要工作是为请求添加cookie、添加固定的header，
 * 比如Host、Content-Length、Content-Type、User-Agent等等，然后保存响应结果的cookie，如果响应使用gzip压缩过，则还需要进行解压。
 * 4.CacheInterceptor(client.internalCache())缓存拦截器：缓存拦截器，获取缓存、更新缓存。如果命中缓存则不会发起网络请求。
 * 5.ConnectInterceptor(client))连接拦截器：连接拦截器，内部会维护一个连接池，负责连接复用、创建连接（三次握手等等）、释放连接以及创建连接上的socket流。
 * 6.client.networkInterceptors()使用者添加的网络拦截器：用户自定义拦截器，通常用于监控网络层的数据传输。
 * 7.CallServerInterceptor(forWebSocket))请求网络服务拦截器：请求拦截器，在前置准备工作完成后，真正发起网络请求，进行IO读写。
 * 最后通过拦截器链，通过责任链模式，依次调用拦截器，最终返回response。
 * <p>
 * 1.OkHttp中使用Okio来进行IO的操作，对这一部分还不是很了解，尤其是IO操作这一块？？？
 * 2.使用Https进行网络通讯，这一部分是如何处理的？？？
 */
public class OkHttpMainActivity extends AppCompatActivity {

    private static final String TAG = "OkHttpMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http_main);

        // socketTest();

        // new Thread(new Runnable() {
        //     @Override
        //     public void run() {
        //         httpTest();
        //     }
        // }).start();
        //
        // httpPostTest();

        interceptTest();

        // getCustomizeTest();
        // postCustomizeTest();
    }

    private void interceptTest() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())   //应用拦截器
                .build();
    }

    /*
    Post请求传递参数总结：
    1.使用FormBody传递键值对参数
    2.使用RequestBody传递Json或File对象
    3.使用MultipartBody同时传递键值对参数和File对象
    4.自定义RequestBody实现流的上传
     */
    private void httpPostTest() {
        String url = "http://www.baidu.com";
        OkHttpClient builder = new OkHttpClient()
                .newBuilder()
                .build(); //利用建造者模式，用于添加自定义属性

        //1.FromBody是RequestBody的实现类
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("json", "JSON");

        //2.RequestBody是抽象类，故不能直接使用，但是他有静态方法create，使用这个方法可以得到RequestBody对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");    //数据类型为json格式，
        String jsonStr = "{\"username\":\"lisi\",\"nickname\":\"李四\"}"; //json数据.
        okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, jsonStr);

        //3.多重body。FromBody传递的是字符串型的键值对，RequestBody传递的是多媒体，二者都传递,此时就需要使用MultipartBody类。其实这个里面还是做了拼接操作
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("groupId", "11")//添加请求头键值对参数
                .addFormDataPart("title", "title")
                .addFormDataPart("file", "图片名字", okhttp3.RequestBody.create(MediaType.parse("image/jpeg"), new File("path")))//添加图片文件
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("token", "myToken")     //添加请求头键值对
                .post(formBody.build()) //添加请求体
                .build();
        final okhttp3.Call call = builder.newCall(request);
        call.enqueue(new okhttp3.Callback() {   //异步
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.e(TAG, "response.code()==" + response.code());  //"response.code()==200；"这个是Http协议里面自带的code
                    Log.e(TAG, "response.message()==" + response.message());
                    //response.body().string()只能调用一次，在第一次时有返回值，第二次再调用时将会返回null。
                    // 原因是：response.body().string()的本质是输入流的读操作，必须有服务器的输出流的写操作时客户端的读操作才能得到数据。而服务器
                    // 的写操作只执行一次，所以客户端的读操作也只能执行一次，第二次将返回null。
                    Log.e(TAG, "res==" + response.body().string());
                }
            }
        });

    }

    private void httpTest() {
        String url = "http://www.baidu.com";
        OkHttpClient client = new OkHttpClient();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .build(); //利用建造者模式，用于添加自定义属性

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()  //默认为GET请求，可以不写
                .build();
        final okhttp3.Call call = client.newCall(request);
        try {
            okhttp3.Response response = call.execute();//同步
            if (response.isSuccessful()) {
                Log.e(TAG, "response.code()==" + response.code());  //"response.code()==200；"这个是Http协议里面自带的code
                Log.e(TAG, "response.message()==" + response.message());
                //response.body().string()只能调用一次，在第一次时有返回值，第二次再调用时将会返回null。
                // 原因是：response.body().string()的本质是输入流的读操作，必须有服务器的输出流的写操作时客户端的读操作才能得到数据。而服务器
                // 的写操作只执行一次，所以客户端的读操作也只能执行一次，第二次将返回null。
                Log.e(TAG, "res==" + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postCustomizeTest() {
        RequestBody body = new RequestBody()
                .add("city", "长沙")
                .add("key", "13cb58f5884f9749287abbead9c658f2");
        Request request = new Request.Builder().url("http://restapi.amap" +
                ".com/v3/weather/weatherInfo").post(body).build();
        new DNHttpClient().newCall(request).enqueue(new Callback() {
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
        Call call = new DNHttpClient().newCall(request);
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

    private void socketTest() {
        //客户端使用Socket进行连接
        Socket socket = null;
        try {
//             socket = SSLSocketFactory.getDefault().createSocket("wwww.baidu.com", 443);
            socket = new Socket("www.weather.com.cn", 80);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(("GET /weather/101250101.shtml HTTP/1.1\r\n" +
                    "Host: www.weather.com.cn\r\n\r\n").getBytes());
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

class LoggingInterceptor implements Interceptor {
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        long t1 = System.nanoTime();
        Log.i("LoggingInterceptor", String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        okhttp3.Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.i("LoggingInterceptor", String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        return response;
    }
}