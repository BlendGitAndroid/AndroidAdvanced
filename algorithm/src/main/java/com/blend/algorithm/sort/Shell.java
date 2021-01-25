package com.blend.algorithm.sort;

/**
 * 希尔排序又称为缩小增量排序，是插入排序的一种威力加强版。
 * 把记录按步长gap分组，对每组记录采用直接插入排序。
 */
class Shell {

    public static void main(String[] args) {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        shellSort(array);
        for (int i : array) {
            System.out.print(i + " ");
        }
    }

    private static void shellSort(int[] array) {
        int gap = array.length / 2;
        while (1 <= gap) {
            //将距离为gap的元素编为一个组，扫描所有组
            for (int i = gap; i < array.length; i++) {  //从第一个gap开始
                int j = 0;
                int temp = array[i];    //将第一个gap的值赋值给temp

                //对距离为gap的元素组进行插入排序，步长为gap，可以对比插入排序
                for (j = i - gap; j >= 0 && temp < array[j]; j = j - gap) { //如果temp小于array[j]，则互换元素
                    array[j + gap] = array[j];
                }
                array[j + gap] = temp;
            }
            gap = gap / 2;
        }
    }
}
