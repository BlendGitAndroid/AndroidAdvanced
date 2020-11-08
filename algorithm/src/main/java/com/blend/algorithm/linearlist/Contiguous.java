package com.blend.algorithm.linearlist;

public class Contiguous {

    public void testSwap() {
        int a = 5;
        int b = 6;
        //1  可读性最好的
//        int t=a;a=b;b=t;
        //2
//        a=a+b;// a=11  b=6
//        b=a-b;// a=11   b=5
//        a=a-b;// a=6    b=5
        //3  性能最优（没有可读性）  无人机  跑步
        a = a ^ b;
        b = a ^ b;
        a = a ^ b;
        System.out.println("a=" + a + "--- b=" + b);


    }

    public int testSreach(int[] array, int des) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == des) {
                return i;
            }
        }
        return -1;
    }

    public void testCards() {
        Cards[] array = new Cards[]{
                new Cards(3, 2), new Cards(2, 9), new Cards(1, 7),
                new Cards(3, 5), new Cards(4, 3)
        };
//        Arrays.sort(array);//100行以上
        bubbleSort(array);
        for (Cards cards : array) {
            System.out.println(cards.toString());
        }
    }

    public void testSort() {
        int[] array = new int[]{3, 2, 5, 8, 1, 9, 4, 6, 7};

        for (int i : array) {
            System.out.print(i + " ");
        }
        System.out.println("\n");
//        bubbleSort(array);
        selectSort(array);
        for (int i : array) {
            System.out.print(i + " ");
        }

    }

    public static void bubbleSort(int[] array) {
        //3 1 5 8 2 9 4 6 7    n*(n-1)/2    n
        for (int i = array.length - 1; i > 0; i--) {
            boolean flag = true;
            for (int j = 0; j < i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
    }

    public static void bubbleSort(Cards[] array) {  //3-5个数据  78
        //1 2 3 4 5 9 4 6 7    n*(n-1)/2   n
        for (int i = array.length - 1; i > 0; i--) {
            boolean flag = true;
            for (int j = 0; j < i; j++) {
                if (array[j].compareTo(array[j + 1]) > 0) {
                    Cards temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
    }

    public static void selectSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            int index = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[index]) {
                    index = j;
                }
            }
            //{1,2,5,8,3,9,4,6,7};
            if (index != i) {//如果已经是最小的，就不需要交换
                int temp = array[index];
                array[index] = array[i];
                array[i] = temp;
            }
        }
    }

}