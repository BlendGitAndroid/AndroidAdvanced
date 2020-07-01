package com.blend.architecture.aop.aspect;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class UserInfoBehaviorTraceAspect {

    private static final String TAG = "UserInfoBehaviorTraceAs";


    @Pointcut("execution(@com.blend.architecture.aop.annotation.UserInfoBehaviorTrace *  *(..))")
    public void methodAnnotatedWithBehaviorTrace() {
    }

    @After("methodAnnotatedWithBehaviorTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e(TAG, "weaveJoinPoint: 被执行");
        return null;
    }

}
