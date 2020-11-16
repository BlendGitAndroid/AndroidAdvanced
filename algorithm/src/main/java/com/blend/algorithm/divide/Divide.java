package com.blend.algorithm.divide;

/**
 * 分治法：
 * <p>
 * 顺序查找：
 * 如果线性表为无序表，即表中元素的排列是无序的，则不管线性表采用顺序存储还是链式存储，都必须使用顺序查找。如果线性表有序，
 * 但采用链式存储结构，则也必须使用顺序查找。
 * <p>
 * <p>
 * 二分查找：
 * 前题条件：数据已经排序
 * 注意：
 * 设计成左闭右开--是一种区间无重复的思想random(０，１)等大量的数学函数，都是设计成左开右闭
 * <p>
 * <p>
 * 快速排序：
 * 应用场景：数据量大并且是线性结构，短处：有大量重复数据的时候，性能不好
 */
public class Divide {

    public static void test() throws Exception {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        int key = 35;

        //二分查找
        System.out.println(binarySearch(array, 0, array.length, key));  //设计成左开右闭的原则
        for (int i : array) {
            System.out.print(i + " ");
        }

        //快速排序
        quickSort(array, 0, array.length - 1);

        //归并排序
        mergeSort(array, 0, array.length - 1);

    }

    /**
     * 二分查找
     */
    public static int binarySearch(int[] array, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while (low <= high) {
            int mid = (low + high) / 2;//取中间
            int midVal = array[mid];
            if (key > midVal) {//去右边找
                low = mid + 1;
            } else if (key < midVal) {//去左边找
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);//low+1表示找不到时停在了第low+1个元素的位置
    }


    /**
     * 快速排序也是利用递归的思想，第一次排序确定第一个元素的位置，然后再在左右两边做递归，最终排好序
     * 这也是利用树的前序遍历
     * <p>
     * 快速排序     31  21  59  68  12  40
     */
    public static void quickSort(int[] array, int begin, int end) {
        if (end - begin <= 0) return;
        int x = array[begin];
        int low = begin;
        int high = end;
        //由于会从两头取数据，需要一个方向
        boolean direction = true;
        L1:
        while (low < high) {
            if (direction) {//从右往左找
                for (int i = high; i > low; i--) {
                    if (array[i] <= x) {
                        array[low++] = array[i];
                        high = i;
                        direction = !direction;
                        continue L1;
                    }
                }
                high = low;//如果上面的if从未进入，让两个指针重合
            } else {
                for (int i = low; i < high; i++) {
                    if (array[i] >= x) {
                        array[high--] = array[i];
                        low = i;
                        direction = !direction;
                        continue L1;
                    }
                }
                low = high;
            }
        }
        //把最后找到的值 放入中间位置
        array[low] = x;
        //开始完成左右两边的操作
        quickSort(array, begin, low - 1);
        quickSort(array, low + 1, end);
    }

    /**
     * 归并排序：利用树的后续遍历
     */
    public static void mergeSort(int array[], int left, int right) {
        if (left == right) {
            return;
        } else {
            int mid = (left + right) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid + 1, right);
        }
    }

    //    0    4   7
    //    1  2  5  9 === 3  4  10  11
    public static void merge(int[] array, int left, int mid, int right) {
        int leftSize = mid - left;
        int rightSize = right - mid + 1;
        //生成数组
        int[] leftArray = new int[leftSize];
        int[] rightArray = new int[rightSize];
        //填充数据
        for (int i = left; i < mid; i++) {
            leftArray[i - left] = array[i];
        }
        for (int i = mid; i <= right; i++) {
            rightArray[i - mid] = array[i];
        }
        //合并
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
