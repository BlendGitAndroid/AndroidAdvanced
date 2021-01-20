package com.blend.algorithm.sort;

/**
 * 归并排序，采用的是分治策略。什么是分治策略呢？
 * 分治策略就是先分后治，分就是将问题分成一些小的问题，然后再递归求解。
 * 治就是将分的阶段得到的答案进行修补到一起。
 * <p>
 * 这个类似于树的后续遍历，感觉树的后续遍历也是分治思想。
 */
class Merge {

    public static void main(String[] args) {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        mergeSort(array, 0, array.length - 1);
        for (int i : array) {
            System.out.print(i + " ");
        }
    }

    private static void mergeSort(int[] array, int left, int right) {
        if (left == right) {
            return;
        }
        int middle = (left + right) / 2;
        mergeSort(array, left, middle);     //先分，进行不断的递归
        mergeSort(array, middle + 1, right);    //先分，进行不断的递归
        merge(array, left, middle + 1, right);  //再治
    }

    //因为对于同一个数组，需要知道归并的双方数组的大小
    private static void merge(int[] array, int left, int middle, int right) {
        //数组的大小
        int leftSize = middle - left;   //归并左边数组的大小
        int rightSize = right - middle + 1; //归并右边数组的大小

        //生成数组
        int[] leftArray = new int[leftSize];
        int[] rightArray = new int[rightSize];

        //填充数据
        for (int i = left; i < middle; i++) {
            leftArray[i - left] = array[i];
        }

        for (int i = middle; i <= right; i++) {
            rightArray[i - middle] = array[i];
        }

        //比较大小
        int i = 0;
        int j = 0;
        int k = left;
        while (i < leftSize && j < rightSize) {
            if (leftArray[i] < rightArray[j]) {
                array[k] = leftArray[i];
                k++;
                i++;
            } else {
                array[k] = rightArray[j];
                k++;
                j++;
            }
        }

        while (i < leftSize) {
            array[k] = leftArray[i];
            k++;
            i++;
        }

        while (j < rightSize) {
            array[k] = rightArray[j];
            k++;
            j++;
        }
    }

}
