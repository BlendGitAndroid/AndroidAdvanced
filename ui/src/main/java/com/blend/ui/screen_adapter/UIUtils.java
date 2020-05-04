package com.blend.ui.screen_adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.lang.reflect.Field;


/**
 * 1.取得实际设备宽高
 * <p>
 * 2.通过实际设备宽高，对参照设备宽高进行比例换算
 * <p>
 * 3.给出系数
 */
public class UIUtils {

    private Context context;

    private static UIUtils utils;

    public static UIUtils getInstance(Context context) {
        if (utils == null) {
            utils = new UIUtils(context);
        }
        return utils;
    }


    //参照宽高
    public final float STANDARD_WIDTH = 720;
    public final float STANDARD_HEIGHT = 1232;

    //当前设备实际宽高
    public float displayMetricsWidth;
    public float displayMetricsHeight;

    private final String DIMEN_CLASS = "com.android.internal.R$dimen";


    private UIUtils(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        //加载当前界面信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        if (displayMetricsWidth == 0.0f || displayMetricsHeight == 0.0f) {
            //获取状态框信息
            int systemBarHeight = getValue(context, "system_bar_height", 48);

            if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                this.displayMetricsWidth = displayMetrics.heightPixels;
                this.displayMetricsHeight = displayMetrics.widthPixels - systemBarHeight;
            } else {
                this.displayMetricsWidth = displayMetrics.widthPixels;
                this.displayMetricsHeight = displayMetrics.heightPixels - systemBarHeight;
            }

        }
    }

    //对外提供系数
    public float getHorizontalScaleValue() {
        return displayMetricsWidth / STANDARD_WIDTH;
    }

    public float getVerticalScaleValue() {

        Log.i("testbarry", "displayMetricsHeight:" + displayMetricsHeight);
        return displayMetricsHeight / STANDARD_HEIGHT;
    }


    public int getValue(Context context, String systemid, int defValue) {

        try {
            Class<?> clazz = Class.forName(DIMEN_CLASS);
            Object r = clazz.newInstance();
            Field field = clazz.getField(systemid);
            int x = (int) field.get(r);
            return context.getResources().getDimensionPixelOffset(x);

        } catch (Exception e) {
            return defValue;
        }
    }
}
