package com.blend.androidadvanced.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
//该注解在另外一个注解上使用
@Target(ElementType.ANNOTATION_TYPE)
public @interface EventBase {
    //  setOnClickListener  订阅
    String listenerSetter();

//    事件以及他的类型

    /**
     * 事件监听的类型
     *
     * @return
     */
    Class<?> listenerType();

    /**
     * 事件处理
     *
     * @return
     */
    String callbackMethod();

}
