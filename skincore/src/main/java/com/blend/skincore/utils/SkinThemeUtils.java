package com.blend.skincore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

import com.blend.skincore.R;


public class SkinThemeUtils {

    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {
            android.support.v7.appcompat.R.attr.colorPrimaryDark
    };

    // 一个是状态栏颜色，一个是导航栏颜色
    private static int[] STATUSBAR_COLOR_ATTRS = {android.R.attr.statusBarColor, android.R.attr.navigationBarColor};

    private static int[] TYPEFACE_ATTRS = {R.attr.skinTypeface};

    // 获得theme中的属性中定义的资源id
    public static int[] getResId(Context context, int[] attrs) {
        int[] resIds = new int[attrs.length];
        // 获得theme中的属性,在自定义View中也经常用到
        TypedArray a = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < attrs.length; i++) {
            // 获得theme中定义的资源id
            resIds[i] = a.getResourceId(i, 0);
        }
        a.recycle();
        return resIds;
    }


    /**
     * @param activity
     */
    public static Typeface getSkinTypeface(Activity activity) {
        //获取字体id
        int skinTypefaceId = getResId(activity, TYPEFACE_ATTRS)[0];
        //去皮肤包中查找，并返回
        return SkinResources.getInstance().getTypeface(skinTypefaceId);
    }


    public static void updateStatusBarColor(Activity activity) {
        //5.0以上才能修改
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        //获得 statusBarColor 与 nanavigationBarColor (状态栏颜色)
        //当与 colorPrimaryDark  不同时 以statusBarColor为准
        int[] statusBarColorResId = getResId(activity, STATUSBAR_COLOR_ATTRS);
        if (statusBarColorResId[0] != 0) {
            activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(statusBarColorResId[0]));
        } else {
            //如果直接在style中写入固定颜色值(而不是 @color/XXX ),或者没有写值,则获得0
            int colorPrimaryDarkResId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (colorPrimaryDarkResId != 0) {
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(colorPrimaryDarkResId));
            }
        }
        if (statusBarColorResId[1] != 0) {
            activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(statusBarColorResId[1]));
        } else {
            //获得 colorPrimaryDark
            int colorPrimaryDarkResId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (colorPrimaryDarkResId != 0) {
                activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(colorPrimaryDarkResId));
            }
        }
    }

}
