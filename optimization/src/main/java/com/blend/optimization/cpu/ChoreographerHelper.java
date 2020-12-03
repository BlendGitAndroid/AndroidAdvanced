package com.blend.optimization.cpu;

import android.util.Log;
import android.view.Choreographer;


/**
 * Android系统每隔16ms发出VSYNC信号，来通知界面进行重绘、渲染，每一次同步的周期约为16.6ms，代表一帧的刷新频率。
 * 通过Choreographer类设置它的FrameCallback函数，当每一帧被渲染时会触发回调FrameCallback.doFrame
 * (long frameTimeNanos) 函数。frameTimeNanos是底层VSYNC信号到达的时间戳 。
 * 通过 ChoreographerHelper 可以实时计算帧率和掉帧数，实时监测App页面的帧率数据，发现帧率过低，还可以自动保存现场堆栈信息。
 */
public class ChoreographerHelper {
    private static final String TAG = "ChoreographerHelper";

    public static void start() {

        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            long lastFrameTimeNanos = 0;

            @Override
            public void doFrame(long frameTimeNanos) {
                //上次回调时间
                if (lastFrameTimeNanos == 0) {
                    lastFrameTimeNanos = frameTimeNanos;
                    Choreographer.getInstance().postFrameCallback(this);
                    return;
                }
                long diff = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000;
                if (diff > 16.6f) {
                    //掉帧数
                    int droppedFrameCount = (int) (diff / 16.6);
                    Log.d(TAG, "doFrame: droppedFrameCount=" + droppedFrameCount);
                }
                lastFrameTimeNanos = frameTimeNanos;
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
