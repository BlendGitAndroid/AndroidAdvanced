package com.blend.algorithm.graph;

/**
 * 并查集：并查集的重要思想在于，用集合中的一个元素代表集合。
 * 管理一系列不相交的集合，并支持两种操作：
 * 合并（Union）：把两个不相交的集合合并为一个集合。
 * 查询（Find）：查询两个元素是否在同一个集合中。
 * <p>
 * 路径压缩：只关心一个元素对应的根节点，那我们希望每个元素到根节点的路径尽可能短。
 */
public class UnionFindSet {

    private int[] par; //表示当前下标的父亲是谁，如par[3] = 1, 3的父亲是1。这里数组的值表示父亲，数组的下标表示自己

    private int[] rank;  //表示当前的树的高度

    //查询树的根
    private int find(int x, int[] par) {
        if (par[x] == x) {
            return x;
        } else {
            //路径压缩，第二次查询可以直接返回x的根而不用递归
            return par[x] = find(par[x], par);
        }
    }

    //合并
    private void unite(int x, int y, int[] par, int[] rank) {
        x = find(x, par);
        y = find(y, par);

        if (x == y) {
            return;
        }

        if (rank[x] < rank[y]) {    //若x的高度小于y的高度，则将x的父亲指向y
            par[x] = y;
        } else {
            par[y] = x; //否则将y的父亲指向x
            if (rank[x] == rank[y]) rank[x]++;  //若x和y的高度一致，则将x的高度加1
        }
    }

    //判断x和y是否属于同一个集合
    private boolean same(int x, int y, int[] par) {
        return find(x, par) == find(y, par);
    }

    public static void test() {
        UnionFindSet unionFindSet = new UnionFindSet();
        int[] parent = new int[6];
        int[] rank = new int[6];

        //初始化的时候，每个数字单独且高度为1
        for (int i = 0; i < 6; i++) {
            parent[i] = i;
            rank[i] = 1;
        }

        unionFindSet.unite(1, 2, parent, rank);
        unionFindSet.unite(0, 1, parent, rank);

        unionFindSet.unite(3, 4, parent, rank);
        unionFindSet.unite(3, 5, parent, rank);

        System.out.println("第一次合并之后：");
        for (int i = 0; i < 6; i++) {
            System.out.print(parent[i] + " ");
        }

        System.out.println();
        System.out.println("是否是同一集合：" + unionFindSet.same(1, 5, parent));
        System.out.println("查找2老大：" + unionFindSet.find(2, parent));
        System.out.println("查找4老大：" + unionFindSet.find(4, parent));

        System.out.println("合并");
        unionFindSet.unite(2, 4, parent, rank);

        System.out.println("合并之后");
        System.out.println("是否是同一集合：" + unionFindSet.same(1, 5, parent));
        System.out.println("查找2老大：" + unionFindSet.find(2, parent));
        System.out.println("查找4老大：" + unionFindSet.find(4, parent));
    }
}
