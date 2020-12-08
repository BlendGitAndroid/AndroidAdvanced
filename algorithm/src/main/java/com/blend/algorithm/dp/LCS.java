package com.blend.algorithm.dp;

import java.util.Stack;

/**
 * 定义：最长公共子序列，英文缩写为LCS（Longest Common Subsequence）。其定义是，一个序列 S ，如果分别是两个或多个已知
 * 序列的子序列，且是所有符合此条件序列中最长的，则 S 称为已知序列的最长公共子序列。
 * 应用：
 * 1)Diff工具。
 * 2)生物信息学应用的基础，比如DNA序列的比较。
 * 3)广泛地应用在版本控制，比如Git用来调和文件之间的改变。
 * 4)描述两段文字之间的“相似度”，即它们的雷同程度，从而能够用来辨别抄袭。
 */
public class LCS {

    public static void test() {
        LCS lcs = new LCS();
        lcs.getLCS("abcbdab", "bdcaba");
    }

    private void getLCS(String x, String y) {
        char[] s1 = x.toCharArray();    //将x转换为字符数组
        char[] s2 = y.toCharArray();    //将y转换为字符数组
        //构建推导图，因为这个推导图可以包含空串，所以长度和宽度都比原来的字符串长度加1
        int[][] array = new int[x.length() + 1][y.length() + 1];
        //先把第一行和第一列填上零，因为空串和任何字符串的最大子串都是0
        for (int i = 0; i < array[0].length; i++) {
            array[0][i] = 0;
        }
        for (int i = 0; i < array.length; i++) {
            array[i][0] = 0;
        }
        //使用动态规划的方式填入数据，相同的取左上加1，不同取上和左的最大值
        for (int i = 1; i < array.length; i++) { //推导图数据是从1开始，但是字符数组是0开始
            for (int j = 1; j < array[i].length; j++) {
                if (s1[i - 1] == s2[j - 1]) { //如果相等，左上角加1填入
                    array[i][j] = array[i - 1][j - 1] + 1;
                } else { //不相等，取上和左边的最大值
                    array[i][j] = max(array[i - 1][j], array[i][j - 1]);
                }
            }
        }
        System.out.println("最长公共子序列推导图：");
        //打印
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }

        //从后往前找到结果
        Stack result = new Stack();
        int i = x.length() - 1;
        int j = y.length() - 1;
        while ((i >= 0) && (j >= 0)) {
            if (s1[i] == s2[j]) {   //如果相等，则存入栈
                result.push(s1[i]);
                i--;
                j--;
            } else {    //如果不相等，找到大的那一个值，相等向左移动。注意数组和String中的位置有一位差
                if (array[i + 1][j + 1 - 1] > array[i + 1 - 1][j + 1]) {
                    j--;
                } else {
                    i--;
                }
            }
        }
        System.out.println("最长公共子序列：");
        while (!result.isEmpty()) {
            System.out.print(result.pop() + " ");
        }
        System.out.println();
    }

    private int max(int a, int b) {
        return Math.max(a, b);
    }
}
