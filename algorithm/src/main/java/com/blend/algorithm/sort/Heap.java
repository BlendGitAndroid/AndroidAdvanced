package com.blend.algorithm.sort;

/**
 * 堆是一个顺序存储的完全二叉树。
 * 其中每个节点的值都不大于它的子节点的值，称为小顶堆。
 * 其中每个节点的值都不小于它的子节点的值，称为大顶堆。
 * 若一个节点的索引是i，则它的左孩子的索引是2i+1，右孩子的索引是2i+2
 * <p>
 * 堆排序是从小到上开始一步一步调整的，所以第一步建立初始堆的时候就是从最后一个孩子的父节点开始找的。
 * 父节点公式：一个节点的索引 i，则父节点为(i - 1)/2。
 * 那么最后一个孩子的父节点是(array.length - 1 - 1) / 2。
 * <p>
 * 所以，堆排序是从最后一个叶子节点的父节点开始的。
 */
class Heap {

    public static void main(String[] args) {
        int[] array = new int[]{1, 3, 4, 5, 2, 6, 9, 7, 8, 0};
        heap(array);
        for (int i : array) {
            System.out.print(i + " ");
        }
    }

    private static void heap(int[] array) {
        //先从数组中间元素开始建立堆
        for (int i = (array.length - 2) / 2; i >= 0; i--) { //先从最后一个孩子节点的父节点开始
            adjust(array, i, array.length);
        }

        for (int i = array.length - 1; i > 0; i--) {
            //最后一个元素和第一个元素交换
            int temp = array[i];
            array[i] = array[0];
            array[0] = temp;

            //筛选
            adjust(array, 0, i);
        }
    }

    /**
     * @param array  数组
     * @param parent 父节点索引
     * @param length 数组长度
     */
    private static void adjust(int[] array, int parent, int length) {
        int tmp = array[parent];    //tmp始终保存父节点的值
        int child = 2 * parent + 1; //先获得左孩子的索引
        while (child < length) {    //如果该节点有左孩子
            //如果有右孩子结点，并且右孩子节点的值大于左孩子节点，则选取右孩子节点
            if (child + 1 < length && array[child] < array[child + 1]) {    //选取孩子节点值大的那一个索引
                child++;
            }

            //如果父节点的值已经大于孩子节点的值，则直接结束
            if (tmp >= array[child]) {
                break;
            }

            //把孩子节点的值赋给父节点
            array[parent] = array[child];

            //选取孩子节点的左孩子节点，继续向下筛选
            parent = child;    //将孩子节点的索引赋值给parent
            child = 2 * child + 1;
        }

        array[parent] = tmp;
    }


}
