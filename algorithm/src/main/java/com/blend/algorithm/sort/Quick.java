package com.blend.algorithm.sort;

/**
 * 快速排序，知道其思想，每做一步之前都要判断i < j
 * 还要注意，每一次的循环都要赋值。
 */
class Quick {

    public static void main(String[] args) {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        myQuickSort(array, 0, array.length - 1);
        for (int i : array) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void myQuickSort(int[] array, int begin, int end) {
        if (begin >= end) {
            return;
        }
        int i = begin;
        int j = end;
        int tmp = array[begin];

        while (i < j) {
            while (i < j && array[j] >= tmp) {
                j--;
            }
            if (i < j) {
                array[i] = array[j];
            }
            while (i < j && array[i] <= tmp) {
                i++;
            }
            if (i < j) {
                array[j] = array[i];
            }
        }
        array[i] = tmp;
        myQuickSort(array, begin, i - 1);
        myQuickSort(array, i + 1, end);
    }
}
