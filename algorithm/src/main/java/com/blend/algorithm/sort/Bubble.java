package com.blend.algorithm.sort;

/**
 * 冒泡排序
 */
class Bubble {
    public static void main(String[] args) {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        bubble(array);
        for (int i : array) {
            System.out.print(i + " ");
        }
    }

    private static int[] bubble(int[] array) {
        int tmp;
        for (int i = 0; i < array.length - 1; i++) {    //当还剩下最后一个数时，不需要排序，所以需要进行n-1次排序
            boolean flag = true;
            for (int j = 0; j < array.length - i - 1; j++) {    //比较0到没有排序的
                if (array[j] > array[j + 1]) {
                    tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
        return array;
    }

}
