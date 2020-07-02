package com.blend.architecture.eventbus.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface CoreSubscribe {
    CoreThreadMode threadMode() default CoreThreadMode.POSTING;
}
