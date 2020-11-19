package com.blend.algorithm.tree;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

/**
 * 哈夫曼树也叫作最优二叉树，在权为w1,w2,…,wn的n个叶子结点的所有二叉树中，带权路径长度WPL最小的二叉树称为赫夫曼树或最优二叉树。
 * <p>
 * 哈夫曼树的应用很广，哈夫曼编码就是其在电讯通信中的应用之一。广泛地用于数据文件压缩的十分有效的编码方法。其压缩率通常在20%～90%
 * 之间。在电讯通信业务中，通常用二进制编码来表示字母或其他字符，并用这样的编码来表示字符序列。
 * 实际应用中各字符的出现频度不相同，用短（长）编码表示频率大（小）的字符，使得编码序列的总长度最小，使所需总空间量最少。
 * <p>
 * 编码：以电文中的字符作为叶子结点构造二叉树。然后将二叉树中结点引向其左孩子的分支标 ‘0’，引向其右孩子的分支标 ‘1’； 每个字符的
 * 编码即为从根到每个叶子的路径上得到的 0, 1 序列。如此得到的即为二进制前缀编码。
 * <p>
 * 译码：从哈夫曼树根开始，对待译码电文逐位取码。若编码是“0”，则向左走；若编码是“1”，则向右走，一旦到达叶子结点，则译出一个字符；
 * 再重新从根出发，直到电文结束。
 */
public class HuffmanTree {

    public void huffmanTest() {
        ArrayList<TreeNode> list = new ArrayList<>();
        TreeNode<String> node1 = new TreeNode<>("A", 6);
        TreeNode<String> node2 = new TreeNode<>("B", 2);
        TreeNode<String> node3 = new TreeNode<>("C", 16);
        TreeNode<String> node4 = new TreeNode<>("D", 25);
        TreeNode<String> node5 = new TreeNode<>("E", 38);
        TreeNode<String> node6 = new TreeNode<>("F", 11);
        list.add(node1);
        list.add(node2);
        list.add(node3);
        list.add(node4);
        list.add(node5);
        list.add(node6);
        createHuffmanTree(list);

        System.out.println("打印哈夫曼树：");
        showHuffman(root);

        getCode(node1);
        getCode(node2);
        getCode(node3);
        getCode(node4);
        System.out.println();
    }


    private TreeNode root;

    /**
     * 构建哈夫曼树
     */
    private TreeNode createHuffmanTree(ArrayList<TreeNode> list) {
        while (list.size() > 1) {
            Collections.sort(list); //每次都进行从大到小排序
            TreeNode left = list.get(list.size() - 1);  //每次取最小的两个
            TreeNode right = list.get(list.size() - 2);
            TreeNode parent = new TreeNode("p", left.weight + right.weight);
            parent.leftChild = left;
            left.parent = parent;
            parent.rightChild = right;
            right.parent = parent;
            list.remove(left);
            list.remove(right);
            list.add(parent);
        }
        root = list.get(0);
        return root;
    }

    /**
     * 打印哈夫曼树，利用树的层次遍历
     */
    private void showHuffman(TreeNode treeNode) {
        LinkedList<TreeNode> list = new LinkedList<>();
        list.offer(treeNode);
        while (!list.isEmpty()) {
            TreeNode node = list.pop();
            System.out.print(node.data + " ");
            if (node.leftChild != null) {
                list.offer(node.leftChild);
            }
            if (node.rightChild != null) {
                list.offer(node.rightChild);
            }
        }
    }

    /**
     * 获取哈夫曼编码
     * 输入一个叶子结点
     */
    private void getCode(TreeNode node) {
        TreeNode treeNode = node;
        Stack<String> stack = new Stack<>();
        while (treeNode != null && treeNode.parent != null) {
            //左0右1
            if (treeNode.parent.leftChild == treeNode) {
                stack.add("0");
            } else if (treeNode.parent.rightChild == treeNode) {
                stack.add("1");
            }
            treeNode = treeNode.parent;
        }
        System.out.println();
        System.out.println("打印哈夫曼编码：" + node.data);
        while (!stack.isEmpty()) {
            System.out.print(stack.pop());
        }
    }


    /**
     * 构建树
     */
    private class TreeNode<T> implements Comparable<TreeNode<T>> {

        T data;
        int weight;
        TreeNode leftChild;
        TreeNode rightChild;
        TreeNode parent;

        public TreeNode(T data, int weight) {
            this.data = data;
            this.weight = weight;
            leftChild = null;
            rightChild = null;
            parent = null;
        }

        @Override
        public int compareTo(TreeNode<T> o) {
            if (this.weight > o.weight) {
                return -1;
            } else if (this.weight < o.weight) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
