package com.blend.algorithm.graph;

/**
 * 最短路径：
 * 最短路问题分为两类：单源最短路和多源最短路。前者只需要求一个固定的起点到各个顶点的最短路径，后者则要求得出任意两个顶点之间
 * 的最短路径。我们先来看多源最短路问题。
 * 多源头最短路径问题：
 * Floyd的时间复杂度显然是O(n^3) ，同时拥有 O(n^2) 的空间复杂度（用n表示点数，m表示边数），都比较高，所以只适用于数据规模较小的情形。
 */
public class ShortestPath {

    public static void test() {

    }

    /**
     *
     */
    private static class Floyd {

        public void test() {
            int vexCount = 4;
            int[][] arcs = new int[vexCount + 1][vexCount + 1];
            int[][] path = new int[vexCount + 1][vexCount + 1];

            /**
             * 初始化图的邻接矩阵
             */
            for (int i = 1; i <= vexCount; i++) {
                for (int j = 1; j <= vexCount; j++) {
                    if (i != j) {
                        arcs[i][j] = Integer.MAX_VALUE;
                    } else {
                        arcs[i][j] = 0;
                    }
                    path[i][j] = j;
                }
            }

            /**
             * 输入图的边集
             */
            arcs[1][2] = 2;
            arcs[1][3] = 6;
            arcs[1][4] = 4;
            arcs[2][3] = 3;
            arcs[3][1] = 7;
            arcs[3][4] = 1;
            arcs[4][1] = 5;
            arcs[4][3] = 12;
            print(arcs, vexCount, 0);

            /**
             * floyd核心算法：
             * if arcs[i][k] + arcs[k][j] < arcs[i][j] then
             *      arcs[i][j] = arcs[i][k] + arcs[k][j]
             */
            for (int k = 1; k <= vexCount; k++) {
                for (int i = 1; i <= vexCount; i++) {
                    for (int j = 1; j <= vexCount; j++) {
                        if (arcs[i][k] < Integer.MAX_VALUE && arcs[k][j] < Integer.MAX_VALUE) {
                            final int d = arcs[i][k] + arcs[k][j];
                            if (d < arcs[i][j]) { //经过k点时i到j的距离比不经过k点的距离更短
                                arcs[i][j] = d; //更新i到j的最短距离
                                path[i][j] = path[i][k]; //更新i到j经过的最后一个点为k点
                            }
                        }
                    }
                }
                print(arcs, vexCount, k);
            }

            printPath(arcs, path, vexCount);
        }

        private static void print(int arcs[][], int vexCount, int index) {
            System.out.println("print array step of " + index + ":");
            for (int i = 1; i <= vexCount; i++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 1; j <= vexCount; j++) {
                    if (arcs[i][j] < Integer.MAX_VALUE) {
                        builder.append(String.format("%5d", arcs[i][j])).append(" ");
                    } else {
                        builder.append(String.format("%5s", "∞")).append(" ");
                    }
                }
                builder.append("\n");
                System.out.println(builder.toString());
            }
        }

        private static void printPath(int arcs[][], int path[][], int vexCount) {
            System.out.println("print path:");
            int temp;
            for (int i = 1; i <= vexCount; i++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 1; j <= vexCount; j++) {
                    builder.append(i).append("->").append(j)
                            .append(", weight: ").append(arcs[i][j]).append(":").append(i);
                    temp = path[i][j];
                    while (temp != j) {
                        builder.append("->").append(temp);
                        temp = path[temp][j];
                    }
                    builder.append("->").append(j).append("\n");
                }
                System.out.println(builder.toString());
            }
        }
    }

}
