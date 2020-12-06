package com.blend.algorithm.graph;

import java.util.LinkedList;

public class Graph {
    private int[] vertices;//顶点集
    private int[][] matrix;//图的边的信息
    private int verticesSize;

    private static final int MAX_WEIGHT = Integer.MAX_VALUE;

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
    private int getFirstNeightBor(int v) {
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
    private int getNextNeightBor(int v, int index) {
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
                System.out.println("viested vertice " + i);
                dfs(i);
            }
        }
    }

    private void dfs(int i) {
        isVisited[i] = true;
        int v = getFirstNeightBor(i);
        while (v != -1) {
            if (!isVisited[v]) {
                System.out.println("visted vertice " + v);
                dfs(v);
            }
            v = getNextNeightBor(i, v);
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
                System.out.println("visited vertice:" + i);
                bfs(i);
            }
        }
    }

    private void bfs(int i) {
        LinkedList<Integer> queue = new LinkedList<>();
        //找第一个邻接点
        int fn = getFirstNeightBor(i);
        if (fn == -1) {
            return;
        }
        if (!isVisited[fn]) {
            isVisited[fn] = true;
            System.out.println("visted vertice:" + fn);
            queue.offer(fn);
        }
        //开始把后面的邻接点都入队
        int next = getNextNeightBor(i, fn);
        while (next != -1) {
            if (!isVisited[next]) {
                isVisited[next] = true;
                System.out.println("visted vertice:" + next);
                queue.offer(next);
            }
            next = getNextNeightBor(i, next);
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

        graph.dfs();

        graph.bfs();
    }

}
