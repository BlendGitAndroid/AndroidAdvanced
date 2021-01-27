package com.blend.algorithm.sort;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 计数排序，非比较排序
 * 使用空间换时间
 */
class Count {
    public static void main(String[] args) {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        int[] result = count(array);
        for (int i : result) {
            System.out.print(i + " ");
        }
    }

    private static int[] count(int[] array) {
        int max = Integer.MIN_VALUE;
        for (int i : array) {
            max = Math.max(i, max); //找到最大值
        }
        int[] count = new int[max + 1]; //长度为max + 1,因为包括索引0
        for (int i : array) {
            count[i]++; //按照索引，对所以的值进行累加
        }
        int[] result = new int[array.length];
        int index = 0;
        for (int i = 0; i < count.length; i++) {
            while (count[i] > 0) {      //如果count[i]大于0
                result[index++] = i;    //将i赋值给result[index]，并将index加1
                count[i]--;
            }
        }
        return result;
    }
}
