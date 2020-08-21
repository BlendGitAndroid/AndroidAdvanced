package com.blend.architecture.glide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.blend.architecture.R;
import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.request.RequestOptions;

import java.io.File;

/**
 * 1.Glide中印象最深的是什么？内存缓存(Lru和弱引用)和磁盘缓存机制。
 * 2.Glide图片写入的顺序和读取的顺序是什么？写：弱引用、Lru、磁盘；读：Lru、弱引用、磁盘。????
 * 3.Glide中图片复用池是怎么设计的？
 * 4.Glide中内存溢出的处理有哪些？内存占用问题？内存优化问题？
 * 5.加载一张高像素的图片（1920*1080），其内部是如何处理的，图片是怎么压缩的；缩略图是怎么处理的。
 * 6.Glide中用的到设计模式？加载不同的资源：策略模式；
 * 7.叫你设计一款图片加载库，你会考虑哪些？缓存、复用池、多种图片加载方式、性能。
 * <p>
 * Glide源码解读：
 * 按照逻辑功能划分，可以把Glide框架分为大概的如下几个部分：
 * 1.加载请求。
 * 2.执行引擎。
 * 3.数据加载器。
 * 4.解码器。
 * 5.编码器。
 * 6.缓存。
 * <p>
 * Glide类：
 * Glide类是DCL单例类，通过Glide#get(Context)方法获取到实例，是通过GlideBuilder创建的，例如设置 BitmapPool、MemoryCache、diskCacheFactory等
 * <p>
 * Glide中的with()方法有五个static重载，可以传入多种上下文，方法体内是调用getRetriever获得RequestManagerRetriever实例对象，再调用其get方法返回一个RequestManager实例。
 * RequestManager with(Context context)
 * RequestManager with(android.app.Activity)
 * RequestManager with(android.app.Fragment)
 * RequestManager with(android.support.v4.app.Fragment)
 * RequestManager with(android.support.v4.app.FragmentActivity)
 * <p>
 * 5个重载的 Glide#with() 方法对应 RequestManagerRetriever中的5个重载的get()方法：
 * 1)如果 with方法的参数为Activity或者Fragment，则最终调用RequestManagerRetriever中的fragmentGet(Context, android.app.FragmentManager)
 * 方法创建RequestManager；
 * 2)如果with方法的参数为android.support.v4.app.Fragment或者android.support.v4.app.FragmentActivity，则最终调用
 * supportFragmentGet(Context, android.support.v4.app.FragmentManager)方法创建RequestManager；
 * 3)如果with方法的参数为Context，则会判断其来源是否属于FragmentActivity及Activity，是则按照上面的逻辑进行处理，
 * 否则最终调用getApplicationManager(Context)方法创建 RequestManager。
 * 4)如果子线程调用 Glide#with()或者系统版本小于 17，则最终会调用getApplicationManager(Context)方法创建 RequestManager。
 * <p>
 * 在子线程调用 Glide#with()方法或传入Context对象为ApplicationContext，此时会创建一个全局唯一的 RequestManager，生命周期与APP周期保持一致。
 * <p>
 * 这三个方法作用都是用来创建RequestManager，前两个方法主要是用来兼容support包中的FragmentActivity、Fragment。
 * <p>
 * 可以得出如下结论：
 * 1)同一个Activity对应一个FragmentManager，一个FragmentManager对应一个RequestManagerFragment，一个 RequestManagerFragment对应一个RequestManager，
 * 所以一个Activity对应一个RequestManager；同一个Fragment同样可得出上述结论；
 * 2)如果Fragment属于Activity，或者Fragment属于Fragment，在 Activity、Fragment中分别创建Glide请求是不会只创建一个RequestManager；
 * 3)子线程发起Glide请求或传入对象为ApplicationContext，则使用全局单例的RequestManager。
 * <p>
 * 那么RequestManager有什么作用呢？
 * 1)创建 RequestBuilder；
 * 2)通过生命周期管理请求的启动结束等。
 * <p>
 * 为什么需要传入一个 FragmentManager参数？
 * 使用 Glide加载图片时，如果当前页面被销毁或者不可见时会停止加载图片，但我们使用Glide加载图片时并没有显示的去设置Glide与当前页面的生命周期关联起来，
 * 只是传了个Context对象，那么Glide是如何通过一个上下文对象就能获取到页面生命周期的呢？创建RequestManager时需要一个FragmentManager参数
 * （全局RequestManager除外），那么再创建RequestManager时会先创建一个不可见的Fragment，通过FM加入到当前页面，用这个不可见的Fragment即可检测
 * 页面的生命周期。代码中保证了每个 Activity/Fragment中只包含一个RequestManagerFragment与一个 RequestManager，所以不管是Activity还是fragment，
 * 最后都会委托给fragmentManager做生命周期的管理。
 * 总结来说with方法的作用就是获得当前上下文，构造出和上下文生命周期绑定的RequestManager，自动管理glide的加载开始和停止。
 * <p>
 *
 * <p>
 * RequestManager#load方法也是一组重载方法，定义在interface ModelTypes<T>接口里，这是一个泛型接口，规定了load想要返回的数据类型，RequestManager类实现了该接口，泛型为Drawable类。
 * 以下为重载的load方法：
 * RequestBuilder<Drawable> load(@Nullable Bitmap bitmap);
 * RequestBuilder<Drawable> load(@Nullable Drawable drawable);
 * RequestBuilder<Drawable> load(@Nullable String string);
 * RequestBuilder<Drawable> load(@Nullable Uri uri);
 * RequestBuilder<Drawable> load(@Nullable File file);
 * RequestBuilder<Drawable> load(@RawRes @DrawableRes @Nullable Integer resourceId);
 * RequestBuilder<Drawable> load(@Nullable URL url);
 * RequestBuilder<Drawable> load(@Nullable byte[] model);
 * RequestBuilder<Drawable> load(@Nullable Object model);
 * RequestManager下的load方法都返回RequestBuilder对象，显然是一个建造者模式，用来构建需要的属性。asDrawable方法调用的as方法实际上是调用RequestBuilder的
 * 构造方法。然后调用RequestBuilder的load将需要加载的图片地址(URL，Bitmap等)传递给RequestBuilder#load()，然后load方法都会调用loadGeneric将不同的参数类
 * 型统一转换为Object类型，最后传给RequestBuilder的Object类的model成员变量。
 * <p>
 * RequestBuilder用来构建请求，例如设置RequestOption、缩略图、加载失败占位图等等。上面说到的RequestManager中诸多的load重载方法，
 * 同样也对应RequestBuilder中的重载load方法，一般来说load方法之后就是调用into方法设置ImageView或者Target，into方法中最后会创建
 * Request，并启动。
 * 总结一下load作用，构造一个RequestBuilder实例，同时传入需要加载的数据源类型。
 * <p>
 *
 * <p>
 * RequestBuilder#into方法也是一组重载方法，
 * ViewTarget<ImageView, TranscodeType> into(@NonNull ImageView view)
 * <Y extends Target<TranscodeType>> Y into(@NonNull Y target)
 * Glide加载图片流程的最后一步，暴露了两种public方法，一个的参数是ImageView，作用是指定图片最后要加载到的位置。首先先检查requestBuilder是否额外设置过
 * ImageView的scaleType属性，如果有则在requestOption里面加上裁剪选项，接着构建一个target实例并创建一个主线程的executor用于获得图片资源在主线程更新UI，
 * 调用private的into方法。
 * 1)创建Target实例是通过工厂方法模式创建，根据传入的不同class类型构造bitmapTarget或者drawableTarget。
 * 2）private的into方法，则是直接创建一个主线程的runnable用于回调target给主线程。先判断是否调用过load方法设置目标资源变量(url等)，如果没有直接抛出异常，接着构建
 * Request实例，同时获得target上的前一个request，如果相同则直接复用前一个request，免去了一些配置步骤，同时为了能顺利完成回调，增加了重试机制。然后对于imageView
 * 来说会先取消之前挂起的任务清除任务资源，然后为target重新绑定request请求，track方法开始执行request任务。
 * 3)RequestManager#track方法，首先会将目标target加入到追踪队列，这个队列里保存了当前activity里所有的target，同时和生命周期进行了绑定，这样做的好处是用生命周期
 * 自动管理了request请求的开始、暂停、结束等操作。第二步执行request请求，真正执行加载图片资源的操作，调用了SingleRequest类的begin方法，SingleRequest实现了
 * Request接口，方法里首先判断是否设置过model(Object类型的url，Bitmap等)，如果没有则直接回调加载失败，然后判断是否正在运行，如果重复请求就直接抛出异常，接着判断
 * 是否Request已经完成，完成则调用onResourceReady,接着给view确定height和width，同时调用onSizeReady，如果状态处于running或者WAITING_FOR_SIZE，调用
 * onLoadStarted。
 * 这三个回调的名字很明显，分别对应Request的三个过程，onSizeReady（准备）、onLoadStarted（开始）、onResourceReady（资源完成）。
 * 1)onSizeReady。通过层层调用，调用到Engine#waitForExistingOrStartNewJob方法，创建DecodeJob实例，运行run方法调用runWrapped()方法，这里做了一个状态机的转换，
 * 按照Stage的流程不断的流转，如果runReason是INITIALIZE，就获取Stage.INITIALIZE的下一个状态，先从RESOURCE_CACHE内存里获取缓存，再从DATA_CACHE磁盘获取缓存，
 * 再从SOURCE数据源取数据。这就是三级缓存的策略。三级缓存的生成对应着三个生成类，通过调用getNextGenerator方法获取DataFetcherGenerator类，调用DataFetcherGenerator
 * #runGenerators()获取数据操作。根据不同的缓存，通过状态模式调用不同的startNext方法，以SourceGenerator#startNext()为列子：会调用loadData方法，根据不同的获取资源
 * 策略加载数据，如在HttpUrlFetcher类里也就是网络请求数据的loadData方法中，会请求url拿到输入流，然后回调给Generator，Generator的onDataReady方法接收到回调之后会根据缓
 * 存策略选择将数据缓存起来或是回调数据给外部。
 * 2)onLoadStarted。当status的状态为running或者WAITING_FOR_SIZE的时候，就会调用该方法，它会调用target的onLoadStarted做一些准备工作，在ImageViewTarget类中就会设
 * 置placeholder和一些加载动画。
 * 3)onResourceReady,这个方法就是最后将获得数据装进ImageView或者返回给target的方法,并调动target的setResource进行数据的设置。
 *
 *
 * 另一个参数是target对象，可以定制化一个target并返回。
 *
 * <p>
 * Request
 * request包下面的是封装的请求，里面有一个Request接口，估计所有的请求都是基于这个接口的。接口定义了对请求的开始、结束、状态获取、回收等
 * 操作，所以请求中不仅包含基本的信息，还负责管理请求。
 * Request主要的实现类有三个：
 * 1)SingleRequest。这个类负责执行请求并将结果反映到Target上。
 * 2)ThumbnailRequestCoordinator。
 * 3)ErrorRequestCoordinator。
 */
public class GlideMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_main);

        ImageView iv = findViewById(R.id.iv);
        ImageView iv1 = findViewById(R.id.iv1);
        ImageView iv2 = findViewById(R.id.iv2);

        new LruCache<String, String>(10) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull String value) {
                return super.sizeOf(key, value);
            }

            @Override
            protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull String oldValue, @Nullable String newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };

//        Bitmap.Config.RGB_565

        Glide.with(this).load("https://ss1.bdstatic" +
                ".com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2669567003," +
                "3609261574&fm=27&gp=0.jpg22222222asads")
                .apply(new RequestOptions().error(R.drawable.ic_launcher_background).placeholder
                        (R.mipmap.ic_launcher).override(500, 500))
                .into(iv);


        Glide.with(this).load(Environment.getExternalStorageDirectory().getPath() + "/glide1.jpg")
                .into(iv1);
        Glide.with(this).load(new File(Environment.getExternalStorageDirectory().getPath() + "/glide2.jpg")).into(iv2);

        //调用原生的Glide加载图片
        com.bumptech.glide.Glide.with(this)
                .load("https://tse3-mm.cn.bing.net/th/id/OIP.Gzze2RWjGPoKUivyJQvTrQHaE7?pid=Api&rs=1")
                .into(iv2);
    }

    public void toNext(View view) {
        startActivity(new Intent(this, GlideSecondActivity.class));
    }
}