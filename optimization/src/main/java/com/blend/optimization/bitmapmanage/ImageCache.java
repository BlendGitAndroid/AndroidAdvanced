package com.blend.optimization.bitmapmanage;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import com.blend.optimization.BuildConfig;
import com.blend.optimization.bitmapmanage.disk.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 管理内存中的图片
 */
public class ImageCache {

    private static final String TAG = "ImageCache";

    private static ImageCache instance;
    private Context context;
    private LruCache<String, Bitmap> memoryCache;
    private DiskLruCache diskLruCache;

    private BitmapFactory.Options options = new BitmapFactory.Options();

    /**
     * 定义一个复用沲
     */
    private static Set<WeakReference<Bitmap>> reuseablePool;


    public static ImageCache getInstance() {
        if (null == instance) {
            synchronized (ImageCache.class) {
                if (null == instance) {
                    instance = new ImageCache();
                }
            }
        }
        return instance;
    }

    //引用队列
    private ReferenceQueue referenceQueue;
    private Thread clearReferenceQueue;
    private boolean shutDown;

    //不需要的内存块，手动Recycler，加快回收队列
    //因为GC会扫描两次，第一次扫描就会加入到引用队列，等到第二次扫描才会回收，这里的处理时等加入到引用队列，就手动回收
    //？？8.0之前Bitmap回收java和native
    private ReferenceQueue<Bitmap> getReferenceQueue() {
        if (null == referenceQueue) {
            //当弱用引需要被回收的时候，会进到这个队列中
            referenceQueue = new ReferenceQueue<Bitmap>();
            //单开一个线程，去扫描引用队列中GC扫到的内容，交到native层去释放
            clearReferenceQueue = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!shutDown) {
                        try {
                            //remove是阻塞式的
                            Reference<Bitmap> reference = referenceQueue.remove();
                            Bitmap bitmap = reference.get();
                            if (null != bitmap && !bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            clearReferenceQueue.start();
        }
        return referenceQueue;
    }

    //dir是用来存放图片文件的路径
    public void init(Context context, String dir) {
        this.context = context.getApplicationContext();

        //复用池
        reuseablePool = Collections.synchronizedSet(new HashSet<>());

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取程序最大可用内存 单位是M
        int memoryClass = am.getMemoryClass();
        Log.e(TAG, "memoryClass: " + memoryClass);

        //参数表示能够缓存的内存最大值，这里为最大内存的1/8  单位是byte
        // (1M == 1024KB 1KB = 1024Byte)
        memoryCache = new LruCache<String, Bitmap>(memoryClass / 8 * 1024 * 1024) {
            /**
             * 在不复用Bitmap时，getByteCount()和getAllocationByteCount返回的结果是一样的。
             *
             * 在通过复用Bitmap来解码图片时，根据不同的API，如果被复用的Bitmap的内存比待分配内存的Bitmap大,
             * getByteCount()：新解码图片占用内存的大小（并非实际内存大小,实际大小是复用的那个Bitmap的大小）
             * getAllocationByteCount()：被复用Bitmap真实占用的内存大小（即mBuffer的长度）。
             *
             * 重写sizeOf方法是因为它会被用来判断缓存的当前大小是否已经达到了预定义的缓存大小，如果超过就需要从
             * 中移除最久没有使用的元素。默认情况下sizeOf返回的时候元素个数，所以如果在创建LruCache时指定的缓
             * 存中的元素个数而非内存空间就可以不重新sizeOf方法。
             *
             */
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //19之前   必需同等大小，才能复用  inSampleSize = 1
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    return value.getAllocationByteCount();
                }
                return value.getByteCount();
            }

            /**
             * 当lru满了，bitmap从lru中移除对象时，将会回调这个方法
             */
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue.isMutable()) {//如果是设置成能复用的内存块，拉到java层来管理，使用弱引用
                    //3.0以下   Bitmap   native
                    //3.0以后   8.0之前   java
                    //8.0开始            native
                    //把这些图片放到一个复用沲中
                    reuseablePool.add(new WeakReference<Bitmap>(oldValue, referenceQueue));
                } else {
                    //oldValue就是移出来的对象
                    oldValue.recycle(); //直接回收内存块
                }


            }
        };

        //valueCount:表示一个key对应valueCount个文件
        try {
            diskLruCache = DiskLruCache.open(new File(dir), 1, 1, 10 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getReferenceQueue();
    }

    public void putBitmapToMemeory(String key, Bitmap bitmap) {
        memoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemory(String key) {
        return memoryCache.get(key);
    }

    public void clearMemoryCache() {
        memoryCache.evictAll();
    }

    //获取复用池中的内容
    public Bitmap getReuseable(int w, int h, int inSampleSize) {
        Bitmap reuseable = null;
        Iterator<WeakReference<Bitmap>> iterator = reuseablePool.iterator();
        while (iterator.hasNext()) {
            Bitmap bitmap = iterator.next().get();
            if (null != bitmap) {
                //可以复用
                if (checkInBitmap(bitmap, w, h, inSampleSize)) {
                    reuseable = bitmap;
                    iterator.remove();
                    break;
                } else {
                    iterator.remove();  //我觉得这里不用移除
                }
            }
        }
        return reuseable;

    }

    private boolean checkInBitmap(Bitmap bitmap, int w, int h, int inSampleSize) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {   //这里要区分版本，19之后宽高和inSampleSize == 1才能复用
            return bitmap.getWidth() == w && bitmap.getHeight() == h && inSampleSize == 1;
        }
        if (inSampleSize >= 1) {
            w /= inSampleSize;
            h /= inSampleSize;
        }
        int byteCount = w * h * getPixelsCount(bitmap.getConfig());
        return byteCount <= bitmap.getAllocationByteCount();
    }

    private int getPixelsCount(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {    //一个像素占32位，4个字节
            return 4;
        }
        return 2;   //其他的占两个字节
    }


    //磁盘缓存的处理

    /**
     * 加入磁盘缓存
     */
    public void putBitMapToDisk(String key, Bitmap bitmap) {
        DiskLruCache.Snapshot snapshot = null;
        OutputStream os = null;
        try {
            snapshot = diskLruCache.get(key);
            //如果缓存中已经有这个文件  不理他
            if (null == snapshot) {
                //如果没有这个文件，就生成这个文件
                DiskLruCache.Editor editor = diskLruCache.edit(key);
                if (null != editor) {
                    os = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                    editor.commit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从磁盘缓存中取
     */
    public Bitmap getBitmapFromDisk(String key, Bitmap reuseable) {
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;
        try {
            snapshot = diskLruCache.get(key);
            if (null == snapshot) {
                return null;
            }
            //获取文件输入流，读取bitmap
            InputStream is = snapshot.getInputStream(0);
            //解码个图片，写入
            options.inMutable = true;   //Bitmap声明为可复用
            options.inBitmap = reuseable;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            if (null != bitmap) {
                memoryCache.put(key, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
        }
        return bitmap;
    }

}












