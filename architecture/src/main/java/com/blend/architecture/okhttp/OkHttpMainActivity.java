package com.blend.architecture.okhttp;

import android.content.Context;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;


/**
 * 设计模式：建造者模式，责任链模式，克隆模式(Call接口)
 * builder模式是，先赋值正确，再创建对象。set是先给予默认值，然后修改。
 * <p>
 * 在网络的请求和返回的过程中，对每一步的过程如拼接请求头，发射，连接等都封装成一个对象，目的是单一职责，为了更好的扩展。
 * <p>
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
 * <p>
 * <p>
 * <p>
 * 1.OkHttp使用步骤：先构建OkHttp对象，构建RequestBody(包括fromBody默认contentType是urlencoded；自定义的匿名RequestBody
 * 能单独请求String/文件等，需要填写相应的contentType；MultipartBody这些内容的混合体，contentType是multipart/form-data，
 * addFormDataPart，将每一部分的内容写成表单键值对，只不过这个键值对的值是一个RequestBody)，最后开始请求。
 * <p>
 * 2.不管同步还是异步，都是先根据Request创建Call实例，然后再调用相应的execute或者enqueue方法。只不过在enqueue中通过callback回调将
 * 结果返回回来。
 * <p>
 * 3.先获取代理服务器信息（没有代理就是直连），根据代理服务器信息创建路由，再由路由选择器选择路由。
 * <p>
 * 4.第一个拦截器：重试拦截器，DNS解析域名返回多个IP，在这里一个一个去尝试重试。但是比如手动设置不允许重试、SSL异常、协议异常等
 * 就不需要重试了。超时重试
 */
public class OkHttpMainActivity extends AppCompatActivity {

    private static final String TAG = "OkHttpMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http_main);

        // socketTest();

        new Thread(new Runnable() {
            @Override
            public void run() {
                httpTest();
            }
        }).start();
        //
        // httpPostTest();

        // interceptTest();

        // getCustomizeTest();
        // postCustomizeTest();

        // test();
    }

    private void test() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)   //连接超时，仅对于TCP的三次握手和TLS握手
                .readTimeout(10, TimeUnit.SECONDS)  //读取超时，服务端返回数据太慢
                .writeTimeout(10, TimeUnit.SECONDS) //发送超时，客户端写数据太慢
                .cache(new Cache(getExternalCacheDir(), 500 * 1024 * 1024))
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request request = chain.request();
                        String url = request.url().toString();
                        Log.i(TAG, "intercept: proceed start: url" + url + ", at " + System.currentTimeMillis());
                        okhttp3.Response response = chain.proceed(request);
                        ResponseBody body = response.body();
                        Log.i(TAG, "intercept: proceed end: url" + url + ", at " + System.currentTimeMillis());
                        return response;
                    }
                }).build();
    }

    private void interceptTest() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor())   //应用拦截器
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
        OkHttpClient builder = new OkHttpClient().newBuilder().build(); //利用建造者模式，用于添加自定义属性

        //1.FromBody是RequestBody的实现类
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("json", "JSON");

        //2.RequestBody是抽象类，故不能直接使用，但是他有静态方法create，使用这个方法可以得到RequestBody对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");    //数据类型为json格式，
        String jsonStr = "{\"username\":\"lisi\",\"nickname\":\"李四\"}"; //json数据.
        okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, jsonStr);

        //请求文件，上传文件
        File file = new File("file");
        okhttp3.RequestBody body1 = okhttp3.RequestBody.create(MediaType.parse("image/png"), file);

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
                Log.e(TAG, "onFailure Thread: " + Thread.currentThread().getName());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e(TAG, "onResponse Thread: " + Thread.currentThread().getName());
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

        // 做SSL Pinner
        CertificatePinner cp = new CertificatePinner.Builder()
                .add("hostname", "hash")
                .build();

        // 当使用代理服务器时，客户端发送的请求会经过代理服务器转发到目标服务器，而响应数据也会经过代理服务器返回给客户端。这样的网络传输
        // 过程中，代理服务器可以拦截、查看和修改请求和响应的数据，从而可能导致数据被窃取或篡改。
        // 为了防止抓包，OkHttp提供了`NO_PROXY`的机制，用于指定不使用代理服务器进行连接的特定主机。具体原理如下：
        // 1. 在OkHttp的配置中设置`NO_PROXY`常量，将目标主机添加到`NO_PROXY`中。可以通过`ProxySelector`或直接设置`Proxy`对象来配置`NO_PROXY`。
        // 2. 当发起请求时，OkHttp会根据请求的目标主机和`NO_PROXY`的规则匹配来决定是否使用代理服务器。如果目标主机与`NO_PROXY`中指定的主机匹配，
        // 则直接进行网络连接，不通过代理服务器。
        // 3. 如果目标主机与`NO_PROXY`中的规则不匹配，则会使用配置的代理服务器进行连接，并按照正常的代理方式进行网络传输。
        // 通过将目标主机添加到`NO_PROXY`中，OkHttp会绕过代理服务器直接连接目标服务器，从而减少了数据经过代理服务器的风险。这样，请求和响应
        // 的数据就不会被代理服务器拦截、查看或修改，提高了数据的安全性。
        // 需要注意的是，`NO_PROXY`仅仅是防止通过代理服务器进行抓包的一种方式，它并不能提供绝对的安全性。如果在客户端或网络中存在其他的抓包手
        // 段或安全问题，仍然可能导致数据被窃取或篡改。因此，在保证数据安全性方面，还需要综合考虑其他安全措施，如使用HTTPS协议、加密数据等。
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .certificatePinner(cp)
                .proxy(Proxy.NO_PROXY)  // 不使用代理
                .build(); //利用建造者模式，用于添加自定义属性

        okhttp3.Request request = new okhttp3.Request.Builder().url(url).get()  //默认为GET请求，可以不写
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
        RequestBody body = new RequestBody().add("city", "长沙").add("key", "13cb58f5884f9749287abbead9c658f2");
        Request request = new Request.Builder().url("http://restapi.amap" + ".com/v3/weather/weatherInfo").post(body).build();
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
        Request request = new Request.Builder().url("http://www.kuaidi100.com/query?type=yuantong&postid=11111111111").build();
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
            outputStream.write(("GET /weather/101250101.shtml HTTP/1.1\r\n" + "Host: www.weather.com.cn\r\n\r\n").getBytes());
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

    /**
     * 在建立HTTPS连接时，Android操作系统会负责处理证书的获取和验证过程。具体来说，当Android客户端与服务器建立连接时，TLS/SSL协议会进行以下操作：
     * 1. 服务器发送证书链：服务器会将证书链发送给客户端。证书链通常由服务器证书、中间证书和根证书组成。
     * 2. 证书验证：Android操作系统会自动验证证书的有效性。这包括检查证书的签名是否有效、证书是否过期、证书是否被吊销等。
     * 3. 主机名验证：在证书验证的过程中，Android操作系统会检查证书中的主机名字段是否与连接的主机名匹配。这是为了防止中间人攻击或使用伪造证书进行欺骗。
     * 4. 证书链的验证：除了验证服务器证书的有效性，Android操作系统还会验证证书链的完整性。这包括检查中间证书是否来自受信任的颁发机构，以及根证书是否
     * 在Android操作系统的信任存储中。
     * 如果证书验证和主机名验证都通过，Android操作系统会继续与服务器完成握手过程，建立加密通道。
     * 需要注意的是，Android操作系统会使用内置的证书信任存储来验证证书的有效性。这个信任存储中包含了一组受信任的根证书，来自主要的颁发机构。如果服务器
     * 的证书由这些受信任的根证书签名，那么验证过程将会成功。如果服务器的证书不是由受信任的根证书签名，那么验证过程将会失败。在这种情况下，开发者可以通
     * 过自定义的方式来处理证书验证和主机名验证，以满足特定的需求。
     *
     *
     * 当Android客户端通过HTTPS连接与服务器建立连接时，TLS/SSL协议会处理整个握手过程，其中包括服务器证书的验证和交换。在握手的过程中，客户端会自动获取
     * 到服务器的证书。
     * 具体来说，当客户端发起HTTPS连接时，服务器会将证书链发送给客户端。客户端会负责验证证书的合法性，并检查证书中的主机名字段是否与连接的主机名匹配。如果
     * 验证通过，客户端会继续与服务器完成握手过程，建立加密通道。
     * 这个自动的过程是由Android操作系统的TLS/SSL实现来处理的，开发者无需手动获取服务器的证书。一般情况下，可以使用Android提供的网络库（如`HttpsURLConnection`、
     * `OkHttpClient`等）来建立HTTPS连接，这些库在底层已经实现了证书验证的逻辑。
     * 但需要注意的是，自动获取服务器证书不代表自动进行证书链的完整性验证。为了确保安全，开发者仍然需要对证书进行验证，包括验证证书的合法性、有效期、颁发机构等信息。
     * 可以使用`HostnameVerifier`来验证服务器证书的主机名字段是否与连接的主机名匹配，或者使用其他方式进行自定义的证书校验逻辑。
     *
     * 操作系统提供的默认证书校验逻辑是为了确保基本的安全性，通常情况下是足够的。然而，有时候默认的校验逻辑可能无法满足特定的安全要求。例如，您可能需要确保与服务器连接
     * 的主机名与服务器证书中的主机名完全匹配，以避免中间人攻击。在这种情况下，您可能需要进行额外的证书校验。因为https还是不能防止中间人攻击，只是能够防止数据被篡改。
     * 因此，需要对证书进行校验，比如匹配主机名。严格一点，进行SSL Pinner，即对证书进行校验，同时还要校验证书的hash值。更严格一点，需要进行双端校验。
     *
     * 在Android中，信任管理器的校验过程是先尝试使用系统证书存储区中的根证书进行验证，如果通过则校验成功。如果服务器证书链中的证书不在系统证书存储区中，或者应用程序使用
     * 了自定义的根证书，那么会使用自定义的根证书进行验证。这样，Android保证了默认情况下使用操作系统提供的根证书进行校验，同时也允许应用程序自定义信任策略和根证书。
     *
     * 要通过操作系统校验自定义证书，可以按照以下步骤进行：
     * 1. 获取服务器证书：在建立连接之前，首先需要获取服务器的证书。可以通过网络通信库或框架提供的接口获取服务器返回的证书。
     * 2. 导入自定义根证书：将自定义的根证书导入到操作系统的信任存储库中。不同操作系统和环境的导入方式可能有所不同，可以参考操作系统的文档或相关开发文档。
     * 3. 配置操作系统信任存储库：将操作系统的信任存储库配置为包含自定义根证书。这样，操作系统会将自定义根证书作为信任根来验证服务器证书。
     * 4. 建立连接并进行校验：使用网络通信库或框架建立与服务器的连接。在建立连接时，操作系统会自动进行证书校验。操作系统会检查证书链的完整性，验证证书的有效性，
     * 检查主机名是否匹配等。
     * 需要注意的是，自定义证书的校验依赖于操作系统的信任存储库。因此，自定义证书只有在操作系统信任的情况下才能通过校验。在进行自定义证书校验之前，需要确保自定
     * 义根证书已经被导入到操作系统的信任存储库中，并且信任存储库的配置正确。
     * 此外，自定义证书的校验仍然受到操作系统校验机制的限制。如果操作系统的校验机制对证书进行了其他限制或策略，自定义证书的校验仍然可能失败。因此，在使用自定义
     * 证书时，需要了解操作系统的校验机制和策略，并确保证书满足相应的要求。
     */

    /**
     * 证书双向校验,使用ssl pinging访问自签名的网站 和 证书双向校验
     * 调用:setCertificates(getAssets().open("srca.cer"))
     *
     * @param context      上下文
     * @param certificates 证书流
     */
    public void setCertificates(Context context, InputStream... certificates) {
        try {
            // 证书工厂
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            // 创建一个证书库，使用的是默认的KeyStore类型
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // 不设置密码
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                // 生成证书别名
                String certificateAlias = Integer.toString(index++);
                // 证书工厂根据证书文件的流生成证书 cert
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                if (certificate != null) {
                    certificate.close();
                }
            }

            // 初始化SSLContext，使用TLS协议
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 创建了TrustManagerFactory实例，使用的是默认的信任管理算法
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // 初始化keystore
            trustManagerFactory.init(keyStore);

            //--- 用于双向校验 --//
            // 创建一个证书库，使用的是默认的KeyStore类型
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // 加载了一个名为"zhy_client.jks"的KeyStore文件，该文件位于应用的assets目录下，加载时使用的密码是"123456"
            clientKeyStore.load(context.getAssets().open("zhy_client.jks"), "123456".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, "123456".toCharArray());

            // 初始化SSLContext
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory())    // 让OkhttpClient去信任这个自定义证书
                    .hostnameVerifier(new HostnameVerifier() {  // 用于主机名校验
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            // `HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session)` 是用
                            // 来验证服务器主机名的方法。它的原理是使用默认的主机名验证器（DefaultHostnameVerifier）来验证
                            // 给定的主机名（hostname）是否与SSL会话（SSLSession）中的服务器主机名匹配。
                            return HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session);
                        }
                    })
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class LoggingInterceptor implements Interceptor {
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        long t1 = System.nanoTime();
        Log.i("LoggingInterceptor", String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        okhttp3.Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.i("LoggingInterceptor", String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        return response;
    }
}