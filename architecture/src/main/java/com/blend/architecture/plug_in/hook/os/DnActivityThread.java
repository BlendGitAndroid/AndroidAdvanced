package com.blend.architecture.plug_in.hook.os;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.blend.architecture.plug_in.hook.LoginActivity;

import java.lang.reflect.Field;
import java.util.List;

import static com.blend.architecture.plug_in.hook.os.Parameter.EXECUTE_TRANSACTION;
import static com.blend.architecture.plug_in.hook.os.Parameter.TARGET_INTENT;

/**
 * 即将要加载的时候，需要把ProxyActivity 给换回来，换成目标LoginActivity，我们也称为【还原操作】
 */
public class DnActivityThread {
    private Context context;

    public DnActivityThread(Context context) {
        this.context = context;
    }

    /**
     * TODO 注意：此方法 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
     *
     * @param mContext
     * @throws Exception
     */
    public void mActivityThreadmHAction(Context mContext) throws Exception {
        context = mContext;
        if (AndroidSdkVersion.isAndroidOS_26_27_28()) {
            do_26_27_28_mHRestore();
        } else if (AndroidSdkVersion.isAndroidOS_21_22_23_24_25()) {
            do_21_22_23_24_25_mHRestore();
        } else {
            throw new IllegalStateException("实在是没有检测到这种系统，需要对这种系统单独处理...");
        }
    }

    /**
     * TODO 给 26_27_28 系统版本 做【还原操作】的
     */
    private final void do_26_27_28_mHRestore() throws Exception {
        //这一步是获取到mH对象
        Class mActivityThreadClass = Class.forName("android.app.ActivityThread");
        Object mActivityThread = mActivityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field mHField = mActivityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Object mH = mHField.get(mActivityThread);

        //获取Handler的Callback回调，利用Hook技术设置CallBack，这里是Handler机制
        Field mCallbackField = Handler.class.getDeclaredField("mCallback");
        mCallbackField.setAccessible(true);
        // 把系统中的Handler.Callback实现 替换成 我们自己写的Custom_26_27_28_Callback
        mCallbackField.set(mH, new Custom_26_27_28_Callback());
    }

    private class Custom_26_27_28_Callback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (EXECUTE_TRANSACTION == msg.what) {
                Object mClientTransaction = msg.obj;

                try {
                    Class<?> mClientTransactionClass = Class.forName("android.app.servertransaction.ClientTransaction");
                    Field mActivityCallbacksField = mClientTransactionClass.getDeclaredField("mActivityCallbacks");
                    mActivityCallbacksField.setAccessible(true);
                    List mActivityCallbacks = (List) mActivityCallbacksField.get(mClientTransaction);

                    /**
                     * 高版本存在多次权限检测，所以添加 需要判断
                     */
                    if (mActivityCallbacks.size() == 0) {
                        return false;
                    }

                    Object mLaunchActivityItem = mActivityCallbacks.get(0);

                    Class mLaunchActivityItemClass = Class.forName("android.app.servertransaction.LaunchActivityItem");

                    // TODO 需要判断
                    if (!mLaunchActivityItemClass.isInstance(mLaunchActivityItem)) {
                        return false;
                    }

                    Field mIntentField = mLaunchActivityItemClass.getDeclaredField("mIntent");
                    mIntentField.setAccessible(true);

                    // 需要拿到真实的Intent
                    Intent proxyIntent = (Intent) mIntentField.get(mLaunchActivityItem);
                    Log.d("hook", "proxyIntent:" + proxyIntent);
                    Intent targetIntent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                    if (targetIntent != null) {
                        //集中式登录
                        SharedPreferences share = context.getSharedPreferences("blend",
                                Context.MODE_PRIVATE);
                        if (share.getBoolean("login", false)) {
                            // 登录  还原  把原有的意图
                            targetIntent.setComponent(targetIntent.getComponent());
                        } else {
                            //跳转到登录界面，并且记录是从哪一个Class跳转过来的
                            ComponentName componentName = new ComponentName(context, LoginActivity.class);
                            targetIntent.putExtra("extraIntent", targetIntent.getComponent().getClassName());
                            targetIntent.setComponent(componentName);
                        }
                        mIntentField.set(mLaunchActivityItem, targetIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    // >>>>>>>>>>>>>>>>>>>>>>>> 下面是 就是专门给 21_22_23_24_25 系统版本 做【还原操作】的

    /**
     * TODO 给 21_22_23_24_25 系统版本 做【还原操作】的
     */
    private void do_21_22_23_24_25_mHRestore() throws Exception {
        Class<?> mActivityThreadClass = Class.forName("android.app.ActivityThread");
        Field msCurrentActivityThreadField = mActivityThreadClass.getDeclaredField("sCurrentActivityThread");
        msCurrentActivityThreadField.setAccessible(true);
        Object mActivityThread = msCurrentActivityThreadField.get(null);

        Field mHField = mActivityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler mH = (Handler) mHField.get(mActivityThread);
        Field mCallbackFile = Handler.class.getDeclaredField("mCallback");
        mCallbackFile.setAccessible(true);

        mCallbackFile.set(mH, new Custom_21_22_23_24_25_Callback());
    }

    private class Custom_21_22_23_24_25_Callback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (Parameter.LAUNCH_ACTIVITY == msg.what) {
                Object mActivityClientRecord = msg.obj;
                try {
                    Field intentField = mActivityClientRecord.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent proxyIntent = (Intent) intentField.get(mActivityClientRecord);
                    // TODO 还原操作，要把之前的LoginActivity给换回来
                    Intent targetIntent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                    if (targetIntent != null) {
                        //集中式登录
                        SharedPreferences share = context.getSharedPreferences("blend",
                                Context.MODE_PRIVATE);
                        if (share.getBoolean("login", false)) {
                            // 登录  还原  把原有的意图    放到realyIntent
                            targetIntent.setComponent(targetIntent.getComponent());
                        } else {

                            String className = targetIntent.getComponent().getClassName();
                            ComponentName componentName = new ComponentName(context, LoginActivity.class);
                            targetIntent.putExtra("extraIntent", className);
                            targetIntent.setComponent(componentName);
                        }
                        // 反射的方式
                        intentField.set(mActivityClientRecord, targetIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}
