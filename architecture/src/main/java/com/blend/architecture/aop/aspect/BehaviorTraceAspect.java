package com.blend.architecture.aop.aspect;

import android.os.SystemClock;
import android.util.Log;

import com.blend.architecture.aop.annotation.BehaviorTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Random;

//定义切面规则
@Aspect //用它声明一个类，表示一个需要执行的切面，是面向切面的配置文件
public class BehaviorTraceAspect {

    private static final String TAG = "BehaviorTraceAspect";

    /*

    1.就在原来的应用中那些注解的地方放到当前切面进行处理,就是代码的织入点
    execution(注解名 注解用到的地方)
     */
    @Pointcut("execution(@com.blend.architecture.aop.annotation.BehaviorTrace *  *(..))") //声明一个切点
    public void methodAnnotatedWithBehaviorTrace() {
    }

    /*
    2.对进入切面的内容如何处理，声明在切点前、中执后、行切面代码(统称为Advice类型)，下面的内容就是织入代码
    @Before 在切入点之前运行
    @After("methodAnnotatedWithBehaviorTrace()")
    @Around 替代原有切点，如果要执行原来代码的话，调用 ProceedingJoinPoint.proceed()
    */
    @Around("methodAnnotatedWithBehaviorTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String value = methodSignature.getMethod().getAnnotation(BehaviorTrace.class).value();
        Object result = joinPoint.proceed();
        long begin = System.currentTimeMillis();
        SystemClock.sleep(new Random().nextInt(2000));
        long duration = System.currentTimeMillis() - begin;
        Log.d(TAG, String.format("%s功能：%s类的%s方法执行了，用时%d ms",
                value, className, methodName, duration));
        return result;
    }

}
