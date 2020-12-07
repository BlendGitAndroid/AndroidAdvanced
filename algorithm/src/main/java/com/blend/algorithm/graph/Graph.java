package com.blend.algorithm.graph;

import java.util.LinkedList;

/**
 * 图是由顶点的有穷非空集合和顶点之间边的集合组成。
 * 概念：有向图，无向图，图的权，连通图，度。
 * 书的数据存储结构：邻接矩阵和邻接表。
 * 图的遍历：
 * 1.深度优先。假设初始状态是图中所有顶点均未被访问，则从某个顶点v出发，首先访问该顶点，然后依次从它的各个未被访问的邻接点出发
 * 深度优先搜索遍历图，直至图中所有和v有路径相通的顶点都被访问到。 若此时尚有其他顶点未被访问到，则另选一个未被访问的顶点作起始点，
 * 重复上述过程，直至图中所有顶点都被访问到为止。
 * 2.广度优先。从图中某顶点v出发，在访问了v之后依次访问v的各个未曾访问过的邻接点，然后分别从这些邻接点出发依次访问它们的邻接点，
 * 并使得“先被访问的顶点的邻接点先于后被访问的顶点的邻接点被访问，直至图中所有已被访问的顶点的邻接点都被访问到。如果此时图中尚有
 * 顶点未被访问，则需要另选一个未曾被访问过的顶点作为新的起始点，重复上述过程，直至图中所有顶点都被访问到为止。
 */
public class Graph {
    private int[] vertices; //顶点集
    private int[][] matrix; //图的边的信息
    private int verticesSize;   //顶点个数

    private static final int MAX_WEIGHT = Integer.MAX_VALUE;    //权重

    private boolean[] isVisited; //标记用来是否被访问过

    public Graph(int verticesSize) {
        this.verticesSize = verticesSize;
        vertices = new int[verticesSize];
        matrix = new int[verticesSize][verticesSize];
        isVisited = new boolean[verticesSize];
        for (int i = 0; i < verticesSize; i++) {
            vertices[i] = i;
        }
    }

    /**
     * 计算v1到v2的权重(路径长度)
     */
    private int getWeight(int v1, int v2) {
        int weight = matrix[v1][v2];
        return weight == 0 ? 0 : (weight == MAX_WEIGHT ? -1 : weight);
    }

    /**
     * 获取顶点
     */
    private int[] getVertices() {
        return vertices;
    }

    /**
     * 获取出度
     */
    private int getOutDegree(int v) {
        int count = 0;
        for (int i = 0; i < verticesSize; i++) {
            if (matrix[v][i] != 0 && matrix[v][i] != MAX_WEIGHT) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取入度
     */
    private int getInDegree(int v) {
        int count = 0;
        for (int i = 0; i < verticesSize; i++) {
            if (matrix[i][v] != 0 && matrix[i][v] != MAX_WEIGHT) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取第一个邻接点
     */
    private int getFirstNeighBar(int v) {
        for (int i = 0; i < verticesSize; i++) {
            if (matrix[v][i] > 0 && matrix[v][i] != MAX_WEIGHT) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取到顶点v的邻接点index的下一个邻接点
     */
    private int getNextNeighBar(int v, int index) {
        for (int i = index + 1; i < verticesSize; i++) {
            if (matrix[v][i] > 0 && matrix[v][i] != MAX_WEIGHT) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 深度优先(很象二叉树的前序)
     */
    private void dfs() {
        for (int i = 0; i < verticesSize; i++) {
            if (!isVisited[i]) {
                System.out.print(" " + i);
                dfs(i);
            }
        }
    }

    private void dfs(int i) {
        isVisited[i] = true;
        int v = getFirstNeighBar(i);
        while (v != -1) {
            if (!isVisited[v]) {
                System.out.print(" " + v);
                dfs(v);
            }
            v = getNextNeighBar(i, v);
        }
    }

    /**
     * 广度优先，理解这种入队的思想
     */
    private void bfs() {
        for (int i = 0; i < verticesSize; i++) {
            isVisited[i] = false;
        }
        for (int i = 0; i < verticesSize; i++) {
            if (!isVisited[i]) {
                isVisited[i] = true;
                System.out.print(" " + i);
                bfs(i);
            }
        }
    }

    private void bfs(int i) {
        LinkedList<Integer> queue = new LinkedList<>();
        //找第一个邻接点
        int fn = getFirstNeighBar(i);
        if (fn == -1) {
            return;
        }
        if (!isVisited[fn]) {
            isVisited[fn] = true;
            System.out.print(" " + fn);
            queue.offer(fn);
        }
        //开始把后面的邻接点都入队
        int next = getNextNeighBar(i, fn);
        while (next != -1) {
            if (!isVisited[next]) {
                isVisited[next] = true;
                System.out.print(" " + next);
                queue.offer(next);
            }
            next = getNextNeighBar(i, next);
        }
        //从队列中取出来一个，重复之前的操作
        while (!queue.isEmpty()) {
            int point = queue.poll();//v1  v2
            bfs(point);
        }

    }

    public static void test() {
        Graph graph = new Graph(5);
        int[] v0 = new int[]{0, 1, 1, MAX_WEIGHT, MAX_WEIGHT};
        int[] v1 = new int[]{MAX_WEIGHT, 0, MAX_WEIGHT, 1, MAX_WEIGHT};
        int[] v2 = new int[]{MAX_WEIGHT, MAX_WEIGHT, 0, MAX_WEIGHT, MAX_WEIGHT};
        int[] v3 = new int[]{1, MAX_WEIGHT, MAX_WEIGHT, 0, MAX_WEIGHT};
        int[] v4 = new int[]{MAX_WEIGHT, MAX_WEIGHT, 1, MAX_WEIGHT, 0};
        graph.matrix[0] = v0;
        graph.matrix[1] = v1;
        graph.matrix[2] = v2;
        graph.matrix[3] = v3;
        graph.matrix[4] = v4;

        System.out.println("深度优先搜素：");
        graph.dfs();

        System.out.println();

        System.out.println("广度优先搜索：");
        graph.bfs();
        System.out.println();
    }

}
