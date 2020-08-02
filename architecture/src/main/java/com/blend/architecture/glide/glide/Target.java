package com.blend.architecture.glide.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;


import com.blend.architecture.glide.glide.request.Request;

import java.lang.ref.WeakReference;


public class Target {


    private static int maxDisplayLength = -1;
    private SizeReadyCallback cb;
    private LayoutListener layoutListener;
    private ImageView view;
    private Request request;

    public void cancel() {
        ViewTreeObserver observer = view.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.removeOnPreDrawListener(layoutListener);
        }
        layoutListener = null;
        cb = null;
    }

    public void onLoadFailed(Drawable error) {
        view.setImageDrawable(error);
    }

    public void onLoadStarted(Drawable placeholderDrawable) {
        view.setImageDrawable(placeholderDrawable);
    }

    public void onResourceReady(Bitmap bitmap) {
//        System.out.println(view.getTag());
//        System.out.println(bitmap.isRecycled()+" "+Thread.currentThread() + " bitmap:" + bitmap.getWidth() + "x" + bitmap
//                .getHeight() + " view:" + view.getWidth() + "x" + view.getHeight());
        view.setImageBitmap(bitmap);

    }

    public void setRequest(Request request) {
        this.request = request;
    }


    private static final class LayoutListener
            implements ViewTreeObserver.OnPreDrawListener {
        private final WeakReference<Target> targetRef;

        LayoutListener(Target sizeDeterminer) {
            targetRef = new WeakReference<>(sizeDeterminer);
        }

        @Override
        public boolean onPreDraw() {
            Target target = targetRef.get();
            if (target != null) {
                target.checkCurrentDimens();
            }
            return true;
        }
    }

    public interface SizeReadyCallback {
        void onSizeReady(int width, int height);
    }


    public Target(ImageView imageView) {
        this.view = imageView;
    }

    public Request getRequest() {
        return request;
    }

    public void getSize(SizeReadyCallback cb) {
        int currentWidth = getTargetWidth();
        int currentHeight = getTargetHeight();
        if (currentHeight > 0 && currentWidth > 0) {
            cb.onSizeReady(currentWidth, currentHeight);
            return;
        }

        this.cb = cb;
        if (layoutListener == null) {
            ViewTreeObserver observer = view.getViewTreeObserver();
            layoutListener = new LayoutListener(this);
            observer.addOnPreDrawListener(layoutListener);
        }
    }

    private int getTargetHeight() {
        int verticalPadding = view.getPaddingTop() + view.getPaddingBottom();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int layoutParamSize = layoutParams != null ? layoutParams.height : 0;
        return getTargetDimen(view.getHeight(), layoutParamSize, verticalPadding);
    }

    private int getTargetWidth() {
        int horizontalPadding = view.getPaddingLeft() + view.getPaddingRight();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int layoutParamSize = layoutParams != null ? layoutParams.width : 0;
        return getTargetDimen(view.getWidth(), layoutParamSize, horizontalPadding);
    }

    private int getTargetDimen(int viewSize, int paramSize, int paddingSize) {
        int adjustedParamSize = paramSize - paddingSize;
        if (adjustedParamSize > 0) {
            return adjustedParamSize;
        }

        int adjustedViewSize = viewSize - paddingSize;
        if (adjustedViewSize > 0) {
            return adjustedViewSize;
        }
        if (!view.isLayoutRequested() && paramSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return getMaxDisplayLength(view.getContext());
        }
        return 0;
    }

    private static int getMaxDisplayLength(Context context) {
        if (maxDisplayLength == -1) {
            WindowManager windowManager =
                    (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point displayDimensions = new Point();
            display.getSize(displayDimensions);
            maxDisplayLength = Math.max(displayDimensions.x, displayDimensions.y);
        }
        return maxDisplayLength;
    }

    void checkCurrentDimens() {
        if (null == cb) {
            return;
        }
        int currentWidth = getTargetWidth();
        int currentHeight = getTargetHeight();
        if (currentHeight <= 0 && currentWidth <= 0) {
            return;
        }
        cb.onSizeReady(currentWidth, currentHeight);
        cancel();
    }
}
