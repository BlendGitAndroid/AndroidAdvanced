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
 * 1.Glide中印象最深的是什么？内存缓存(Lru和弱引用)和磁盘缓存机制(Resource和Data)。
 * 2.Glide图片写入的顺序和读取的顺序是什么？
 * 读取：弱引用(活动资源)、Lru内存缓存、磁盘；
 * 存储：磁盘、弱引用(活动资源)、Lru内存缓存。
 * 3.Glide中图片复用池是怎么设计的？
 * 4.Glide中内存溢出的处理有哪些？内存占用问题？内存优化问题？
 * 5.加载一张高像素的图片（1920*1080），其内部是如何处理的，图片是怎么压缩的；缩略图是怎么处理的。
 * 6.Glide中用的到设计模式？加载不同的资源：策略模式；单列模式，工厂方法模式，建造者模式，策略模式，代理模式，状态模式。
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
 * 总结：发起网络请求前先判断是否有内存缓存，有则直接从内存缓存那里获取数据进行显示，没有则判断是否有磁盘缓存；有磁盘缓存则直接从磁盘缓存那里获取数据进行显示，没有才发起网络请求；
 * 网络请求成功后将返回的数据存储到内存与磁盘缓存（如果有配置），最后将返回的输入流解码成Drawable显示在ImageView上。
 *
 * <p>
 *
 *
 * <p>
 * 缓存策略：
 * 1)ActiveResources，第一级缓存，表示当前正在活动中的资源。Engine#load方法中构建好Key之后第一件事就是去这个缓存中获取资源，获取到则直接返回，获取不到才继续从其他缓存
 * 中寻找。当资源加载成功，或者通过缓存中命中资源后都会将其放入ActivityResources中，资源被释放时移除出ActivityResources。由于其中的生命周期较短，所以没有大小限制。
 * ActiveResources中通过一个Map来存储数据，数据保存在一个虚引用（WeakReference）中， 定义为Map<Key, WeakReference<EngineResource<?>>>。此外还有一个引用队列：
 * 每当向activeResource中添加一个WeakReference对象时都会将resourceReferenceQueue和这个WeakReference关联起来，用来跟踪这个WeakReference的gc，一旦这个弱引用被
 * gc掉，就会将它从activeResource中移除，ReferenceQueue的具体作用可以自行谷歌，大概就是用来跟踪弱引用（或者软引用、虚引用）是否被gc的。
 * 那么ReferenceQueue具体是在何时去判断WeakReference是否被gc了呢？
 * Handler机制大家应该都知道，但不知道大家有没有用过MessageQueue.IdleHandler这个东西，可以调用MessageQueue#addIdleHandler添加一个MessageQueue.IdleHandler对象，
 * Handler会在线程空闲时调用这个方法。resourceReferenceQueue在创建时会创建一个Engine#RefQueueIdleHandler对象并将其添加到当前线程的MessageQueue中，ReferenceQueue
 * 会在IdleHandler回调的方法中去判断activeResource中的WeakReference是不是被gc了，如果是，则将引用从activeResource中移除。
 * <p>
 * 2)MemorySizeCalculator，用来计算BitmapPool、ArrayPool以及MemoryCache大小的。
 * <p>
 * 缓存机制：
 * 一.缓存机制。默认情况下，Glide在加载图片之前会依次检查是否有以下缓存：也就是说Glide中实际有四级缓存，前两个属于内存缓存，后两个属于磁盘缓存。以上每步是按顺序检查的，
 * 检查到哪一步有缓存就直接返回图片，否则继续检查下一步。如果都没有缓存，则Glide会从原始资源（File、Uri、远程图片 url 等）中加载图片。
 * 1)活动资源 (Active Resources)：正在使用的图片。
 * 2)内存缓存 (Memory cache)：内存缓存中的图片。
 * 3)资源类型（Resource）：磁盘缓存中转换过后的图片。
 * 4)数据来源 (Data)：磁盘缓存中的原始图片。
 * 二.缓存Key。缓存功能必然要有一个唯一的缓存Key用来存储和查找对应的缓存数据。通过传入 model（File、Uri、远程图片 url 等）、签名、宽高（这里的宽高是指显示
 * 图片的View的宽高，不是图片的宽高）等参数，然后通过EngineKeyFactory构建了一个EngineKey对象（即缓存Key），然后EngineKey通过重写equals()与
 * hashCode()方法来保证缓存Key的唯一性。
 * 问题：虽然决定缓存Key的参数很多，但是加载图片的代码写好后这些参数都是不会变的。很多人遇到的 “服务器返回的图片变了，但是前端显示的还是以前的图片”的问题就是这个原因，
 * 因为虽然服务器返回的图片变了，但是图片url还是以前那个，其他决定缓存Key的参数也不会变，Glide就认为有该缓存，就会直接从缓存中获取，而不是重新下载，所以显示的还是以前的图片。
 * 解决方案就是（1）图片url不要固定也就是说如果某个图片改变了，那么该图片的url也要跟着改变，（2）禁用缓存，前端加载图片的时候设置禁用内存与磁盘缓存，这样每次加载都会重新下载
 * 最新的。
 * 三.活动资源缓存。ActiveResources里面主要包含了一个HashMap的相关操作，然后HashMap中保存的值又是弱引用来引用的，也就是说这里是采用一个弱引用的 HashMap来缓存活动资源。
 * 首先从HashMap中获取ResourceWeakReference（继承了弱引用），然后从弱引用中获取了活动资源（获取活动资源），即正在使用的图片。也就是说正在使用的图片实际是通过弱引用
 * 维护，然后保存在HashMap中的。使用图片的时候，acquired变量+1，这个变量用来记录图片被引用的次数。该变量除了acquire()方法中做了+1操作，还在release()方法中做了-1的操作，
 * 当acquired减到0的时候，又回调了Engine#onResourceReleased()。在onResourceReleased()方法中首先将活动资源从弱引用的HashMap中移除（清理活动资源），然后将它缓存到内
 * 存缓存中（存储内存缓存）。也就是说，release()方法主要是释放资源，当我们从一屏滑动到下一屏的时候，上一屏的图片就会看不到，这个时候就会调用该方法。还有我们关闭当前显示图片的
 * 页面时会调用onDestroy()方法，最终也会调用该方法。这两种情况很明显是不需要用到该图片了，那么理所当然的会调用release()方法来释放弱引用的 HashMap中缓存的活动资源。
 * 四.内存Lru缓存。使用Lru算法实现的内存缓存。内存缓存主要是获取活动资源、清理活动资源、获取内存缓存、存储内存缓存。其中清理内存缓存的操作LRU算法已经自动帮我们实现了，存贮
 * 活动缓存则是在从网络请求中返回的数据解码后存储的。
 * 五.磁盘缓存。一共有五种策略：
 * 1)ALL：既缓存原始图片，也缓存转换过后的图片。
 * 2)NONE：不缓存任何内容。
 * 3)DATA：只缓存原始图片。
 * 4)RESOURCE：只缓存转换过后的图片。
 * 5)AUTOMATIC：默认策略，它会尝试对本地和远程图片使用最佳的策略。如果是远程图片，则只缓存原始图片；如果是本地图片，那么只缓存转换过后的图片。
 * <p>
 * 六.数据来源。该级缓存只缓存原始图片。
 * <p>
 * <p>
 * <p>
 * 自定义GlideModule
 * 1.自定义图片加载类型。
 * 2.在Glide中使用OkHttp加载。但是需要引入依赖，默认使用的是HttpURLConnection。
 * <p>
 * Glide.with()中做了3件事情：
 * 1.如果有配置GlideModel，则利用反射和代理模式配置Glide。
 * 2.初始化Glide，配置线程池和BitmapPool。设置资源类型-加载方式-返回类型-解码方式的映射
 * 3.设置一个空的Fragment，添加声明周期回调，内存监听回调，任务监听，网络状态变化回调。
 * <p>
 * Lru算法在Glide中有三种实现：内存缓存，磁盘缓存，BitmapPool缓存。
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

        // 调用原生的Glide加载图片
        // com.bumptech.glide.Glide.with(this)
        //         .load("https://tse3-mm.cn.bing.net/th/id/OIP.Gzze2RWjGPoKUivyJQvTrQHaE7?pid=Api&rs=1")
        //         .into(iv2);
        //
        // // //自定义图片加载类型
        // GlideApp.with(this)
        //         .load(new MyAppGlideModule.CachedImage("https://tse3-mm.cn.bing.net/th/id/OIP.Gzze2RWjGPoKUivyJQvTrQHaE7?pid=Api&rs=1"))
        //         .apply(com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
        //         .into(iv2);
    }

    public void toNext(View view) {
        startActivity(new Intent(this, GlideSecondActivity.class));
    }
}