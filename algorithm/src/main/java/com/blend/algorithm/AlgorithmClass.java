package com.blend.algorithm;

import com.blend.algorithm.tree.AVLTree;
import com.blend.algorithm.tree.HuffmanTree;

/**
 * 数据结构：计算机存储、组织数据的方式。
 * 算法：指解题方案的准确而完整的描述，是一系列解决问题的清晰指令。
 * 一个算法的优劣可以用空间复杂度与时间复杂度来衡量。
 * 程序好坏=空间复杂度+时间复杂度+应用场景(重要)
 */
public class AlgorithmClass {

    public static void algorithm(){
        //数据结构与算法
        // MyStack.hanoi(3, "A", "B", "C");

        //猴子偷桃
        // MyStack.monkeyStealPeach();

        //哈夫曼树
        new HuffmanTree().huffmanTest();

        //启发式寻路
        // startActivity(new Intent(this, HeuristicMainActivity.class));

        //平衡二叉搜索树
        new AVLTree().avl();
    }

}