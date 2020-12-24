package com.blend.algorithm.generic;


class GenericInterfaceImpl1<T> implements GenericInterface<T> {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public T next(T t) {
        return t;
    }
}
