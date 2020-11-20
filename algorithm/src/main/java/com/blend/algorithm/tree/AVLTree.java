package com.blend.algorithm.tree;

import java.util.LinkedList;

/**
 * AVL树，也称平衡二叉搜索树(平衡二叉排序树)，AVL是其发明者姓名简写。AVL树属于树的一种，而且它也是一棵二叉搜索树，不同的是
 * 它通过一定机制能保证二叉搜索树的平衡，平衡的二叉搜索树的查询效率更高。
 * <p>
 * 特点：
 * 1.AVL树是一棵二叉搜索树。
 * 2.AVL树的左右子节点也是AVL树。
 * 3.AVL树拥有二叉搜索树的所有基本特点。
 * 4.每个节点的左右子节点的高度之差的绝对值最多为1，即平衡因子为范围为[-1,1]。
 * 平衡因子Balance Factor(bf)：结点的左子树的深度减去右子树的深度，那么显然-1<=bf<=1;
 * <p>
 * 作用：
 * 对于一般的二叉搜索树（Binary Search Tree），其期望高度（即为一棵平衡树时）为log2n，其各操作的时间复杂度（O(log2n)）
 * 同时也由此而决定。但是，在某些极端的情况下（如在插入的序列是有序的时），二叉搜索树将退化成近似链或链，此时，其操作的时间复
 * 杂度将退化成线性的，即O(n)。我们可以通过随机化建立二叉搜索树来尽量的避免这种情况，但是在进行了多次的操作之后，由于在删除时，
 * 我们总是选择将待删除节点的后继代替它本身，这样就会造成总是右边的节点数目减少，以至于树向左偏沉。这同时也会造成树的平衡性受
 * 到破坏，提高它的操作的时间复杂度。
 * <p>
 * AVL的插入和删除：平衡二叉树的失衡调整主要是通过旋转最小失衡子树来实现的。
 * 最小失衡子树：在新插入的结点向上查找，以第一个平衡因子的绝对值超过1的结点为根的子树称为最小不平衡子树。也就是说，一棵失衡的树，
 * 是有可能有多棵子树同时失衡的，如下。而这个时候，我们只要调整最小的不平衡子树，就能够将不平衡的树调整为平衡的树。
 * <p>
 * 插入按照如下基本步骤进行：
 * 1.按照二叉搜索树的方式增加节点，新增节点称为一个叶节点。
 * 2.从新增节点开始，回溯到第一个失衡节点(如果回溯到根节点，还没有失衡节点，就说明该树已经符合AVL性质。)
 * 3.找到断的边，也就是哪边比较大，并确定断边的方向。
 * 4.以断边的下一个孩子为根节点，确定两个子树中的哪一个深度大(左子树还是右子树)。
 * (这两棵子树的深度不可能相等，而且深度大的子树包含有新增节点。想想为什么)，如果相等，说明原来的树就不是平常二叉搜索树。
 * 5.如果第2步和第3步中的方向一致(都为左或者都为右)，需要单旋转以失衡节点为根节点的子树。否则，双旋转以失衡节点为根节点的子树。
 * <p>
 * AVL树的插入方式：
 * LL插入方式：右单旋
 * RR插入方式：左单旋
 * LR插入方式：左右双旋
 * RL插入方式：右左双旋
 * AVL的删除：比较复杂，而红黑树适合删除
 */
public class AVLTree<E extends Comparable<E>> {

    private Node<E> root;
    private int size = 0;
    private static final int LH = 1;    //如果左边大
    private static final int RH = -1;   //如果右边大
    private static final int EH = 0;    //如果两边一样大

    public void avl() {
        Integer[] nums = {5, 8, 2, 0, 1, -2};
        AVLTree<Integer> tree = new AVLTree<>();
        for (int i = 0; i < nums.length; i++) {
            tree.insertElement(nums[i]);
        }
        showAVL((Node) tree.root);
    }

    private void left_rotate(Node<E> x) {
        if (x != null) {
            Node<E> y = x.right;//先取到Y结点
            // 1。把贝塔作为X的右孩子
            x.right = y.left;
            if (y.left != null) {
                y.left.parent = x;
            }
            // 2。把Y移到原来X的位置
            y.parent = x.parent;
            if (x.parent == null) {
                root = y;
            } else {
                if (x.parent.left == x) {
                    x.parent.left = y;

                } else if (x.parent.right == x) {
                    x.parent.right = y;
                }
            }
            //3。X作为Y的左孩子
            y.left = x;
            x.parent = y;
        }
    }

    private void right_rotate(Node<E> y) {
        if (y != null) {
            Node<E> yl = y.left;

            //step1
            y.left = yl.right;
            if (yl.right != null) {
                yl.right.parent = y;
            }

            // step2
            yl.parent = y.parent;
            if (y.parent == null) {
                root = yl;
            } else {
                if (y.parent.left == y) {
                    y.parent.left = yl;
                } else if (y.parent.right == y) {
                    y.parent.right = yl;
                }
            }
            // step3
            yl.right = y;
            y.parent = yl;
        }
    }

    private void rightBalance(Node<E> t) {
        Node<E> tr = t.right;
        switch (tr.balance) {
            case RH://新的结点插入到t的右孩子的右子树中
                left_rotate(t);
                t.balance = EH;
                tr.balance = EH;
                break;
            case LH://新的结点插入到t的右孩子的左子树中
                Node<E> trl = tr.left;
                switch (trl.balance) {
                    case RH:
                        t.balance = LH;
                        tr.balance = EH;
                        trl.balance = EH;
                        break;
                    case LH:
                        t.balance = EH;
                        tr.balance = RH;
                        trl.balance = EH;
                        break;
                    case EH:
                        tr.balance = EH;
                        trl.balance = EH;
                        t.balance = EH;
                        break;

                }
                right_rotate(t.right);
                left_rotate(t);
                break;

        }
    }

    //如果是左平衡树
    private void leftBalance(Node<E> t) {
        Node<E> tl = t.left;
        switch (tl.balance) {
            case LH:    //如果是LL插入，直接右旋
                right_rotate(t);
                tl.balance = EH;
                t.balance = EH;
                break;
            case RH:    //如果是LR插入，则分三种情况分别计算平衡因子，然后先左旋再右旋
                Node<E> tlr = tl.right;
                switch (tlr.balance) {
                    case LH:
                        t.balance = RH;
                        tl.balance = EH;
                        tlr.balance = EH;
                        break;
                    case RH:
                        t.balance = EH;
                        tl.balance = LH;
                        tlr.balance = EH;
                        break;
                    case EH:
                        t.balance = EH;
                        tl.balance = EH;
                        tlr.balance = EH;
                        break;

                    default:
                        break;
                }
                left_rotate(t.left);
                right_rotate(t);
                break;


        }
    }

    private boolean insertElement(E element) {
        Node<E> t = root;
        if (t == null) {
            root = new Node<E>(element, null);
            size = 1;
            root.balance = 0;
            return true;
        } else {
            //开始找到要插入的位置
            int cmp = 0;
            Node<E> parent;
            Comparable<? super E> e = (Comparable<? super E>) element;
            do {
                parent = t;
                cmp = e.compareTo(t.elemet);
                if (cmp < 0) {  //比较之后，小于0的在左边，大于0的在右边
                    t = t.left;
                } else if (cmp > 0) {
                    t = t.right;
                } else {
                    return false;
                }
            } while (t != null);
            //判断大小之后，开始插入数据
            Node<E> child = new Node<E>(element, parent);
            if (cmp < 0) {
                parent.left = child;
            } else {
                parent.right = child;
            }
            //节点已经放到了树上
            //检查平衡，回溯查找
            while (parent != null) {
                cmp = e.compareTo(parent.elemet);
                // 这个平衡因子会随着插入结点变化，由于这个对象，每次插入都会重新计算平衡因子，并且计算后的
                // 平衡因子会保存到这个对象上，用于下次插入结点的判断
                if (cmp < 0) {
                    parent.balance++;
                } else {
                    parent.balance--;
                }
                if (parent.balance == 0) { //如果插入后还是平衡树，不用调整
                    break;
                }
                if (Math.abs(parent.balance) == 2) {
                    //出现了平衡的问题，需要修正
                    fixAfterInsertion(parent);
                    break;
                } else {
                    parent = parent.parent;
                }
            }
        }
        size++;
        return true;
    }

    private void showAVL(Node root) {
        LinkedList<Node> list = new LinkedList<Node>();
        list.offer(root);//队列放入
        while (!list.isEmpty()) {
            Node node = list.pop();//队列的取出
            System.out.println(node.elemet);
            if (node.left != null) {
                list.offer(node.left);
            }
            if (node.right != null) {
                list.offer(node.right);
            }
        }
    }

    private void fixAfterInsertion(Node<E> parent) {
        if (parent.balance == 2) {
            leftBalance(parent);
        }
        if (parent.balance == -2) {
            rightBalance(parent);
        }
    }


    private class Node<E extends Comparable<E>> {
        E elemet;
        int balance = 0;//平衡因子
        Node<E> left;
        Node<E> right;
        Node<E> parent;

        public Node(E elem, Node<E> pare) {
            this.elemet = elem;
            this.parent = pare;
        }

        public E getElemet() {
            return elemet;
        }

        public void setElemet(E elemet) {
            this.elemet = elemet;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public Node<E> getLeft() {
            return left;
        }

        public void setLeft(Node<E> left) {
            this.left = left;
        }

        public Node<E> getRight() {
            return right;
        }

        public void setRight(Node<E> right) {
            this.right = right;
        }

        public Node<E> getParent() {
            return parent;
        }

        public void setParent(Node<E> parent) {
            this.parent = parent;
        }


    }


}
