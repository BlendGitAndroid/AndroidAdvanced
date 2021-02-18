package com.blend.algorithm.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

class HashMapTest {
    public static void main(String[] args) {
        notSafe();
    }

    /**
     * 线程不安全，只要是在多线程下：
     * 1.put方法moCount++出错。
     * 2.扩容期间取出的值不准确，扩容会取出null。
     * 3.同时 put 碰撞导致数据丢失。
     * 4.可见性问题无法保证。
     * 5.死循环造成 CPU 100%。
     */
    private static void notSafe() {
        final Map<Integer, String> map = new HashMap<>();

        final Integer targetKey = 0b1111_1111_1111_1111; // 65 535
        final String targetValue = "v";
        map.put(targetKey, targetValue);

        new Thread(() -> {
            IntStream.range(0, targetKey).forEach(key -> map.put(key, "someValue"));
        }).start();

        while (true) {
            if (null == map.get(targetKey)) {
                throw new RuntimeException("HashMap is not thread safe.");
            }
        }
    }
}
