package com.blend.algorithm.sorttree;

import java.util.NoSuchElementException;

/**
 * 二叉排序树，又叫二叉查找树，搜索树
 * 主要是对二叉排序树的增删查和遍历，做这些操作的时候，需要注意这一个节点的操作要处理三个指针：左右孩子和父节点
 */
public class SortTree {

    //根节点
    public TreeNode root;

    /**
     * 添加节点，肯定添加到一个叶子节点上
     */
    public TreeNode put(int data) {
        if (root == null) {
            TreeNode node = new TreeNode(data);
            root = node;
            return node;
        }
        TreeNode parent = null;     //这里需要有一个parent，需要知道每一个节点的父节点
        TreeNode node = root;
        //找到要放入的位置
        while (node != null) {
            parent = node;
            if (data < node.data) {
                node = node.leftChild;
            } else if (data > node.data) {
                node = node.rightChild;
            } else {//是重复值 就不理会了
                return node;
            }
        }
        //生成一个节点放入
        TreeNode newNode = new TreeNode(data);
        if (data < parent.data) {
            parent.leftChild = newNode;
        } else {
            parent.rightChild = newNode;
        }
        newNode.parent = parent;

        return newNode;
    }


    /**
     * 中序遍历
     */
    public void midOrderTraverse(TreeNode root) {
        if (root == null) {
            return;
        }
        //LDR
        midOrderTraverse(root.leftChild);
        System.out.print(root.data + " ");
        midOrderTraverse(root.rightChild);
    }

    /**
     * 查找一个节点
     */
    public TreeNode searchNode(int data) {
        if (root == null) {
            return null;
        }
        TreeNode node = root;
        while (node != null) {
            if (node.data == data) {
                return node;
            } else if (data > node.data) {
                node = node.rightChild;
            } else if (data < node.data) {
                node = node.leftChild;
            }
        }
        return null;
    }


    /**
     * 删除节点
     * 要删除的节点在树上是一定存在的才删除
     */
    public void delNode(TreeNode node) {
        if (node == null) {
            throw new NoSuchElementException();
        } else {
            //先得到父亲，方便后面的操作
            TreeNode parent = node.parent;
            //1.叶子
            if (node.leftChild == null && node.rightChild == null) {
                //特别的情况:1.树上只有一个节点或是空树
                if (parent == null) {
                    root = null;
                } else if (parent.rightChild == node) {
                    parent.rightChild = null;
                } else if (parent.leftChild == node) {
                    parent.leftChild = null;
                }
                node.parent = null;
            } else if (node.leftChild != null && node.rightChild == null) {
                //2.只有左孩子
                if (parent == null) {//如果要删除的是根
                    node.parent = null;
                    node.leftChild.parent = null;
                    root = node.leftChild;
                } else {
                    if (parent.leftChild == node) {//要删除的节点是父亲的左边
                        node.leftChild.parent = parent;
                        parent.leftChild = node.leftChild;
                    } else {//要删除的节点是父亲的右边
                        node.leftChild.parent = parent;
                        parent.rightChild = node.leftChild;
                    }
                    node.parent = null;
                }

            } else if (node.leftChild == null && node.rightChild != null) {
                //3.只有右孩子
                if (parent == null) {//如果要删除的是根
                    node.parent = null;
                    node.rightChild.parent = null;
                    root = node.rightChild;
                } else {
                    if (parent.leftChild == node) {//要删除的节点是父亲的左边
                        node.rightChild.parent = parent;
                        parent.leftChild = node.rightChild;
                    } else {//要删除的节点是父亲的右边
                        node.rightChild.parent = parent;
                        parent.rightChild = node.rightChild;
                    }
                    node.parent = null;
                }
            } else {//4。有左右两个孩子
                if (node.rightChild.leftChild == null) {    //1.如果被删除节点的右子树的左子树为空，就直接补上右子树
                    node.rightChild.leftChild = node.leftChild;
                    if (parent == null) {
                        root = node.rightChild;
                    } else {
                        if (parent.leftChild == node) {
                            parent.leftChild = node.rightChild;
                            //
                        } else {
                            parent.rightChild = node.rightChild;
                            //
                        }
                    }
                    node.parent = null;
                } else {//2.否则就要补上右子树的左子树上最小的一个
                    TreeNode leftNode = getMinLeftTreeNode(node.rightChild);
                    //1
                    leftNode.leftChild = node.leftChild;
                    //2
                    TreeNode leftNodeP = leftNode.parent;
                    leftNodeP.leftChild = leftNode.rightChild;
                    //3
                    leftNode.rightChild = node.rightChild;
                    //4
                    if (parent == null) {
                        root = leftNode;
                    } else {
                        if (parent.leftChild == node) {
                            parent.leftChild = leftNode;
                            //
                        } else {
                            parent.rightChild = leftNode;
                            //
                        }
                    }
                }
            }
        }
    }

    private TreeNode getMinLeftTreeNode(TreeNode node) {
        TreeNode curRoot = null;
        if (node == null) {
            return null;
        } else {
            curRoot = node;
            while (curRoot.leftChild != null) {
                curRoot = curRoot.leftChild;
            }
        }
        return curRoot;
    }


    public static class TreeNode {
        int data;
        TreeNode leftChild;
        TreeNode rightChild;
        TreeNode parent;

        public TreeNode(int data) {
            this.data = data;
            this.leftChild = null;
            this.rightChild = null;
            this.parent = null;
        }
    }

}
