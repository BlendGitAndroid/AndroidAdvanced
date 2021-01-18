package com.blend.algorithm.sort;

/**
 * 基本思想：每一步将一个待排序的数据插入到前面已经排好序的有序序列中，直到插完所有元素为止。
 */
class Insert {

    public static void main(String[] args) {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        insertSort(array);
        for (int i : array) {
            System.out.print(i + " ");
        }
    }

    private static void insertSort(int[] array) {
        int tmp;
        int i = 0;
        int j = 0;
        for (i = 1; i < array.length; i++) {
            tmp = array[i];
            for (j = i - 1; j >= 0 && array[j] > tmp; j--) {
                array[j + 1] = array[j];
            }
            array[j + 1] = tmp;
        }
    }

}
