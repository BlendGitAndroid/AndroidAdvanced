package com.blend.routercore;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.blend.routerannotation.model.RouteMeta;
import com.blend.routercore.callback.NavigationCallback;
import com.blend.routercore.exception.NoRouteFoundException;
import com.blend.routercore.template.IRouteGroup;
import com.blend.routercore.template.IRouteRoot;
import com.blend.routercore.template.IService;
import com.blend.routercore.utils.ClassUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * 注解的作用：注解主要是提取类或者字段的信息，用于读取数据，更深入的就是自定义注解处理器，可以在编译时候生成自己的语法树，
 * 生成额外的java信息或者文件。
 * <p>
 * 路由原理：为了实现组件间互不引用，但能够相互跳转，需要跳转到哪个界面，根据Activity的注解，去路由表中匹配，匹配到了进行跳转。
 * 1）实现Router路由表并分组。利用注解，根据APT和javaPoet,在不同的模块下，分别建立分组表和路由表。分组表利用Map，根据不同的
 * 模块进行分组，Map的key是模块的名字，Value是路由表；同理路由表也是Map存储，key是不同组件（Activity/Service）的注解，Value
 * 是对应的类信息。
 * 2）初始化。首先获得本程序所有apk应用存放数据的目录，加载APK中的dex，根据目录获得所有apt生成的路由分组表全类名的List（因为不同
 * module下的分组表和路由表的包名都是一样的）。遍历分组表的List，根据反射实现，利用策略模式，将分组信息加入仓库中。
 * 3）数据准备。首先根据传入的路径信息和Bundle信息，生成路径和组别，并新建跳转类PostCard。之后在仓库中根据路径找到路由表，第一次由于
 * 路由表仓库中还没有，同理根据反射实现，利用策略模式，将路由表信息加入仓库中。之后为PostCard设置要跳转的activity或IService实现类。
 * 4）跳转。利用Intent，传入全类名进行跳转。
 * 5）以上是Activity的跳转，对于方法的调用，需要用到接口。同样，也是根据反射拿到每一个接口的实现类，保存在库中，并给PostCard设置实现类，
 * 将实现类返回给调用者，从而调用相应的实现类方法。
 * 6）对于参数的传递，使用自动注入，在页面间传递数据。上一个Activity通过Bundle传值，在下一个Activity使用Extra注解就能拿到传来的值。
 * 同样也是通过注解来实现的加JavaPoet，用来生成getIntent及参数传递的模块化代码，就是得到从上一个Activity通过Bundle传来的值。
 *
 *
 * <p>
 * 为什么需要分组：因为当项目变得越来越大庞大的时候，为了便于管理和减小首次加载路由表过于耗时的问题，我们对所有的路由进行分组。
 * 初始化只加载分组表使用的时候，如果使用A分组就去加载A分组下所有的路由信息，而不会去加载B分组。比如A组有100个
 * 路由信息，B有200个。如果不分组，你Map中就需要加载300个路由信息，当用户可能根本就不需要进入B分组的页面，加载B分组的路由信息
 * 除了浪费了内存。在ARouter中会要求路由地址至少需要两级，如"/xx/xx",一个模块下可以有多个分组。这里我们就将路由地址定为必须大于等于两级，
 * 其中第一级是group。
 *
 *
 * <p>
 * 自定义注解处理器：Annotation Processor是javac的一个工具，它用来在编译时扫描和处理注解。通过Annotation Processor可以获
 * 取到注解和被注解对象的相关信息，然后根据注解自动生成Java代码，省去了手动编写，提高了编码效率。
 * 刚接触Annotation Processor的同学可能会遇到找不到AbstractProcessor类的问题，大概率是因为直接在Android项目里边引用了
 * AbstractProcessor，然而由于Android平台是基于OpenJDK的，而OpenJDK中不包含Annotation Processor的相关代码。因此，
 * 在使用Annotation Processor时，必须在新建Module时选择Java Library，处理注解相关的代码都需要在Java Library模块下完成。
 *
 *
 * <p>
 * JavaPoet:是一个Java API用于生成Java源文件。能用于自动生成一些模板化的java文件，提高工作效率，简化流程。
 * <p>
 * Arouter也是使用显示Intent跳转的，Intent里面有一个Component属性，主要有两个属性，一个是要启动组件的包名packageName，另外一个
 * 是要启动组件的全类名。
 * Intent intent = new Intent();
 * intent.setClassName("包名", "全类名");
 * startActivity(intent);
 * 一般Intent是启动本应用的类，因为本应用的类的包名都是同一个，但是如果知道了其他应用的包名和全名类，也是可以用Intent启动的。
 *
 * <p>
 * 如果不使用JavaPoet,还可以使用JavaFileObject，JavaFileObject是java文件对象，可以直接创建，通过流的形式，对文件进行编写。
 */
public class BlendRouter {
    private static final String TAG = "BlendRouter";
    private static final String ROUTE_ROOT_PAKCAGE = "com.blend.routercore";
    private static final String SDK_NAME = "BlendRouter";
    private static final String SEPARATOR = "$$";
    private static final String SUFFIX_ROOT = "Root";
    private static Application mContext;

    private static BlendRouter instance;


    public static BlendRouter getInstance() {
        synchronized (BlendRouter.class) {
            if (instance == null) {
                instance = new BlendRouter();
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param application
     */
    public static void init(Application application) {
        mContext = application;
        try {
            loadInfo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "初始化失败!", e);
        }
    }


    private static void loadInfo() throws InterruptedException, IOException, PackageManager.NameNotFoundException,
            ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        //获得所有apt生成的路由类的全类名 (分组表)
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                // root中注册的是分组信息 将分组信息加入仓库中，根据反射实现，利用策略模式，加载所有分组表
                ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.groupsIndex);
            }
        }
//        for (Map.Entry<String, Class<? extends IRouteGroup>> stringClassEntry : Warehouse
//                .groupsIndex.entrySet()) {
//            Log.e(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry
//                    .getValue() + "]");
//        }

    }


    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    public Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new Postcard(path, group);
        }
    }


    /**
     * 获得组别
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据跳卡跳转页面
     *
     * @param context
     * @param postcard
     * @param requestCode
     * @param callback
     */
    protected Object navigation(Context context, final Postcard postcard, final int requestCode,
                                final NavigationCallback callback) {
        try {
            prepareCard(postcard);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            if (null != callback) {
                callback.onLost(postcard);
            }
            return null;
        }
        if (null != callback) {
            callback.onFound(postcard);
        }

        switch (postcard.getType()) {
            case ACTIVITY:
                final Context currentContext = null == context ? mContext : context;
                final Intent intent = new Intent(currentContext, postcard.getDestination());
                intent.putExtras(postcard.getExtras());
                int flags = postcard.getFlags();
                if (-1 != flags) {
                    intent.setFlags(flags);
                } else if (!(currentContext instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //可能需要返回码
                        if (requestCode > 0) {
                            ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode, postcard.getOptionsBundle());
                        } else {
                            ActivityCompat.startActivity(currentContext, intent, postcard.getOptionsBundle());
                        }

                        if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) && currentContext instanceof Activity) {
                            //老版本
                            ((Activity) currentContext).overridePendingTransition(postcard.getEnterAnim(), postcard.getExitAnim());
                        }
                        //跳转完成
                        if (null != callback) {
                            callback.onArrival(postcard);
                        }
                    }
                });
                break;
            case ISERVICE:
                return postcard.getService();
            default:
                break;
        }
        return null;
    }

    /**
     * 准备卡片
     *
     * @param card
     */
    private void prepareCard(Postcard card) {
        RouteMeta routeMeta = Warehouse.routes.get(card.getPath());
        //还没准备的
        if (null == routeMeta) {
            //创建并调用 loadInto 函数,然后记录在仓库
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(card.getGroup());
            if (null == groupMeta) {
                throw new NoRouteFoundException("没找到对应路由: " + card.getGroup() + " " + card.getPath());
            }
            IRouteGroup iGroupInstance;
            try {
                iGroupInstance = groupMeta.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("路由分组映射表记录失败.", e);
            }
            iGroupInstance.loadInto(Warehouse.routes);
            //已经准备过了就可以移除了 (不会一直存在内存中)
            Warehouse.groupsIndex.remove(card.getGroup());
            //再次进入 else
            prepareCard(card);
        } else {
            //类 要跳转的activity 或IService实现类
            card.setDestination(routeMeta.getDestination());
            card.setType(routeMeta.getType());
            switch (routeMeta.getType()) {
                case ISERVICE:
                    Class<?> destination = routeMeta.getDestination();
                    IService service = Warehouse.services.get(destination);
                    if (null == service) {
                        try {
                            service = (IService) destination.getConstructor().newInstance();
                            Warehouse.services.put(destination, service);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    card.setService(service);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 注入
     *
     * @param instance
     */
    public void inject(Activity instance) {
        ExtraManager.getInstance().loadExtras(instance);
    }


}
