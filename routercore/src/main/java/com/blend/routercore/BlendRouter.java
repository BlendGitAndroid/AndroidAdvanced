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
 * 路由原理：在组件化开发中，
 * <p>
 * 为什么需要分组：初始化只加载分组表使用的时候，如果使用A分组就去加载A分组下所有的路由信息，而不会去加载B分组。比如A组有100个
 * 路由信息，B有200个。如果不分组，你Map中就需要加载300个路由信息，当用户可能根本就不需要进入B分组的页面，加载B分组的路由信息
 * 除了浪费了内存。
 * <p>
 * 自定义注解处理器：Annotation Processor是javac的一个工具，它用来在编译时扫描和处理注解。通过Annotation Processor可以获
 * 取到注解和被注解对象的相关信息，然后根据注解自动生成Java代码，省去了手动编写，提高了编码效率。
 * 刚接触Annotation Processor的同学可能会遇到找不到AbstractProcessor类的问题，大概率是因为直接在Android项目里边引用了
 * AbstractProcessor，然而由于Android平台是基于OpenJDK的，而OpenJDK中不包含Annotation Processor的相关代码。因此，
 * 在使用Annotation Processor时，必须在新建Module时选择Java Library，处理注解相关的代码都需要在Java Library模块下完成。
 * <p>
 * JavaPoet:是一个Java API用于生成Java源文件。能用于自动生成一些模板化的java文件，提高工作效率，简化流程。
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


    private static void loadInfo() throws InterruptedException, IOException, PackageManager
            .NameNotFoundException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        //获得所有 apt生成的路由类的全类名 (路由表)
        Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR +
                    SUFFIX_ROOT)) {
                // root中注册的是分组信息 将分组信息加入仓库中
                ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto
                        (Warehouse.groupsIndex);
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
                            ActivityCompat.startActivityForResult((Activity) currentContext, intent,
                                    requestCode, postcard.getOptionsBundle());
                        } else {
                            ActivityCompat.startActivity(currentContext, intent, postcard
                                    .getOptionsBundle());
                        }

                        if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) &&
                                currentContext instanceof Activity) {
                            //老版本
                            ((Activity) currentContext).overridePendingTransition(postcard
                                            .getEnterAnim()
                                    , postcard.getExitAnim());
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
            Class<? extends IRouteGroup> groupMeta = Warehouse.groupsIndex.get(card
                    .getGroup());
            if (null == groupMeta) {
                throw new NoRouteFoundException("没找到对应路由: " + card.getGroup() + " " +
                        card.getPath());
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
