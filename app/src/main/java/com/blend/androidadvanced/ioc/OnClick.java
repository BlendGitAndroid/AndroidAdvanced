package com.blend.androidadvanced.ioc;


import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventBase(listenerSetter = "setOnClickListener"
        , listenerType = View.OnClickListener.class
        , callbackMethod = "onClick")
public @interface OnClick {
    int[] value() default -1;
}












