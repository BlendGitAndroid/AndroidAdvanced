package com.blend.algorithm.sort;

class Select {
    public static void main(String[] args) {
        int[] array = new int[]{1, 7, 4, 9, 3, 2, 6, 5, 8};
        selectSort(array);
        for (int i : array) {
            System.out.print(i + " ");
        }
    }

    private static void selectSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int index = i;
            for (int j = i; j < array.length; j++) {
                if (array[j] < array[index]){
                    index = j;
                }
            }
            int tmp = array[i];
            array[i] = array[index];
            array[index] = tmp;
        }
    }


}
