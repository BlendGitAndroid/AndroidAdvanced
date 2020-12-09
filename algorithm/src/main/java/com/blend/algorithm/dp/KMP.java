package com.blend.algorithm.dp;


/**
 * KMP算法：
 */
public class KMP {

    public void test() {
        String str = "ababcabcbababcabacaba";
        String dest = "ababcaba";
        int[] array = kmpNext(dest);
        System.out.println(kmp(str, dest, array));
    }

    /**
     * KMP算法
     */
    private int[] kmpNext(String dest) {
        int[] next = new int[dest.length()];
        next[0] = 0;
        //开始推出next
        for (int i = 1, j = 0; i < dest.length(); i++) {
            //3
            while (j > 0 && dest.charAt(j) != dest.charAt(i)) {
                j = next[j - 1];
            }
            //1
            if (dest.charAt(i) == dest.charAt(j)) {
                j++;
            }
            //2
            next[i] = j;
        }
        return next;
    }

    private int kmp(String str, String dest, int[] next) {
        for (int i = 0, j = 0; i < str.length(); i++) {
            while (j > 0 && str.charAt(i) != dest.charAt(j)) {
                j = next[j - 1];
            }
            if (str.charAt(i) == dest.charAt(j)) {
                j++;
            }
            if (j == dest.length()) {//结束
                return i - j + 1;
            }
        }
        return 0;
    }
}
