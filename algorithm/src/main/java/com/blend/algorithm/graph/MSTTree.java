package com.blend.algorithm.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * 最小生成树(Minimum Spanning Tree,MST)：在连通图的所有生成树中，所有边的代价和最小的生成树，称为最小生成树。
 * 最小生成树可以用kruskal（克鲁斯卡尔）算法或prim（普里姆）算法求出。
 * 三个性质：
 * 1.最小生成树是树，因此其边数等于顶点数减一，且树内一定不会有环。
 * 2.对给定的图，其最小生成树可以不唯一，但边权之和一定唯一。
 * 3.最小生成树是在无向图上面生成的，因此其根节点可以是这颗树上面的任意一个节点。如果题目中涉及最小生成树的输出，为了让
 * 生成树唯一，一般会直接给出根节点，只需要给出节点作为根节点来求解最小生成树即可。
 * <p>
 * Kruskal算法：
 * 此算法可以称为“加边法”，初始最小生成树边数为0，每迭代一次就选择一条满足条件的最小代价边，加入到最小生成树的边集合里。
 * 1.把图中的所有边按代价从小到大排序；
 * 2.把图中的n个顶点看成独立的n棵树组成的森林；
 * 3.按权值从小到大选择边，所选的边连接的两个顶点ui,vi,应属于两颗不同的树(防止出现环)，则成为最小生成树的一条边，并将这两颗树合并作为一颗树。
 * 4.重复(3),直到所有顶点都在一颗树内或者有n-1条边为止。
 * <p>
 * Prim算法：
 * 此算法可以称为“加点法”，每次迭代选择代价最小的边对应的点，加入到最小生成树中。算法从某一个顶点s开始，逐渐长大覆盖整个连通网的所有顶点。
 * 图的所有顶点集合为V；初始令集合u={s},v=V−u;
 * 在两个集合u,v能够组成的边中，选择一条代价最小的边(u0,v0)，加入到最小生成树中，并把v0并入到集合u中。
 * 重复上述步骤，直到最小生成树有n-1条边或者n个顶点为止。
 */
public class MSTTree {

    public static void test() {
        Prime prime = new Prime();
        Graph graph = prime.init();
        prime.MST_Prime(graph);
    }

    /*
     * 首先我们给出图的存储结构
     */
    private static class Graph {

        /*
         * 点的存储
         */
        private List<String> vex;
        /*
         * 边的存储
         */
        private int edges[][];

        public Graph(List<String> vex, int[][] edges) {
            this.vex = vex;
            this.edges = edges;
        }

        public List<String> getVex() {
            return vex;
        }

        public void setVex(List<String> vex) {
            this.vex = vex;
        }

        public int[][] getEdges() {
            return edges;
        }

        public void setEdges(int edges[][]) {
            this.edges = edges;
        }

        public int getVexNum() {
            return vex.size();
        }

        public int getEdgeNum() {
            return edges.length;
        }
    }


    //普里姆算法
    private static class Prime {

        int m = Integer.MAX_VALUE;

        int[][] edges = {
                {0, 3, 1, m, 4},
                {3, 0, 2, m, m},
                {1, 2, 0, 5, 6},
                {m, m, 5, 0, m},
                {4, m, 6, m, 0},
        };

        //打印最小生成树
        void MST_Prime(Graph G) {
            int vexNum = G.getVexNum(); //节点个数
            int[] min_weight = new int[vexNum];//当前结果树到所有顶点的最短距离
            int[] adjvex = new int[vexNum];//adjvex[3]=0，代表C是通过A加入结果树的（0是A的下标，3是C的下标）
            /*初始化两个辅助数组*/
            for (int i = 0; i < vexNum; i++) {
                min_weight[i] = (G.getEdges())[0][i];//第一个顶点到其余顶点的距离，不通的距离为无限大
                adjvex[i] = 0;
            }
            int min_edg;//当前挑选的最小权值
            int min_vex = 0;//最小权值对应的节点下标
            /*循环剩余n-1个点*/
            for (int i = 1; i < vexNum; i++) {
                min_edg = Integer.MAX_VALUE;
                for (int j = 1; j < vexNum; j++) {
                    if (min_weight[j] != 0 && min_weight[j] < min_edg) {
                        //寻找还没有被挑选进来的，最小权重的点
                        min_edg = min_weight[j];
                        min_vex = j;
                    }
                }
                min_weight[min_vex] = 0;//纳入结果树
                /*修改对应辅助数组的值*/
                for (int j = 0; j < vexNum; j++) {
                    if (min_weight[j] != 0 && (G.getEdges())[min_vex][j] < min_weight[j] && (G.getEdges())[min_vex][j] > 0) {
                        min_weight[j] = (G.getEdges())[min_vex][j];
                        adjvex[j] = min_vex;
                    }
                }
                int pre = adjvex[min_vex];
                int end = min_vex;
                System.out.println("(" + G.getVex().get(pre) + "," + G.getVex().get(end) + ")");
            }
        }

        //初始化图
        Graph init() {
            List<String> vex = new ArrayList<String>();
            vex.add("A");
            vex.add("B");
            vex.add("C");
            vex.add("D");
            vex.add("E");
            Graph graph = new Graph(vex, edges);
            return graph;
        }

    }


}
