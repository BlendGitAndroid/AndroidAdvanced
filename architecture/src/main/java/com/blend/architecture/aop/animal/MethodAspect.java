package com.blend.architecture.aop.animal;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect //声明了本类是一个AspectJ配置文件
public class MethodAspect {

    private static final String TAG = "MethodAspect";

    /*指定了一个代码织入点，注解内的call(* com.wandering.sample.aspectj.Animal.fly(..)) 是一个切点表达式，
    第一个*号表示返回值可为任意类型，后跟包名+类名+方法名，括号内表示参数列表， .. 表示匹配任意个参数，参数类型为任何类型，
    这个表达式指定了一个时机：在Animal类的fly方法被调用时。
    */
    @Pointcut("call(* com.blend.architecture.aop.animal.Animal.fly(..))")
    public void callMethod() {

    }

    //声明Advice类型为Before并指定切点为上面callMethod方法所表示的那个切点
    @Before("callMethod()")
    public void beforeMethodCall(JoinPoint joinPoint) {
        Log.e(TAG, "beforeMethodCall: " + joinPoint.getTarget().toString());    //实际织入的代码
    }
}
