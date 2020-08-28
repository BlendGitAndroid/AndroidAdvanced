package com.blend.architecture.rxjava.rxjava;


//转换功能  T 原有数据类型  R  转换后的数据类型
public interface Function<T, R> {
    //对输入值运用一些计算得方式并返回其他一些值
    R apply(T t);
}
