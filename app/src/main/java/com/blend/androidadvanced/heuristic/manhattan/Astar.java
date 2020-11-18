package com.blend.androidadvanced.heuristic.manhattan;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Astar {
    public final static int DIRECT_VALUE = 10;
    public final static int OBLIQUE_VALUE = 14;
    //开放列表  白格子
    private Queue<Node> openList = new PriorityQueue<Node>();
    //关闭列表  黄格子
    private List<Node> closeList = new ArrayList<Node>();

    /**
     * 计算H值
     */
    private int calcH(Coord end, Coord coord) {
        return Math.abs(end.x - coord.x) + Math.abs(end.y - coord.y);
    }

    /**
     * 判断是否为终点
     */
    private boolean isEndNode(Coord end, Coord coord) {
        return coord != null && end.equals(coord);
    }

    /**
     * 从open中查找
     */
    private Node findNodeInOpen(Coord coord) {
        if (coord == null || openList.isEmpty()) return null;
        for (Node node : openList) {
            if (node.coord.equals(coord)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 判断是否在close中
     */
    private boolean isCoordInClose(Coord coord) {
        return coord != null && isCoordInClose(coord.x, coord.y);
    }

    private boolean isCoordInClose(int x, int y) {
        if (closeList.isEmpty()) return false;
        for (Node node : closeList) {
            if (node.coord.x == x && node.coord.y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个位置是否可以选择
     */
    private boolean canAddNodeToOpen(MapInfo mapInfo, int x, int y) {
        //是否在地图中
        if (x < 0 || x >= mapInfo.width || y < 0 || y >= mapInfo.hight) return false;
        //判断是否能过
        if (mapInfo.map[y][x] == MapUtils.WALL) return false;
        //判断是否已经被 选择过的
        if (isCoordInClose(x, y)) return false;
        return true;
    }

    /**
     * 算法开始
     */
    public void start(MapInfo mapInfo) {
        if (mapInfo == null) return;
        //清空以前的所有的内容
        openList.clear();
        closeList.clear();
        //开始搜索
        openList.add(mapInfo.start);
        //开始向周围扩展
        moveNodes(mapInfo);
    }

    private void moveNodes(MapInfo mapInfo) {
        while (!openList.isEmpty()) {
            if (isCoordInClose(mapInfo.end.coord)) {//如果已经是终点了
                //统计结果
                calcPath(mapInfo.map, mapInfo.end);
            }
            //把open中最小的一个取出来 ,放到close中
            Node current = openList.poll();
            closeList.add(current);
            //开始扩展
            addNeighborNodeInOpen(mapInfo, current);
        }
    }

    private void calcPath(int[][] map, Node end) {
        MapUtils.path = new Path();
        if (end != null) {
            MapUtils.path.moveTo(end.coord.x * 80 + 40, end.coord.y * 80 + 40);
        }
        while (end != null) {//把结果入栈
            MapUtils.path.lineTo(end.coord.x * 80 + 40, end.coord.y * 80 + 40);
            MapUtils.result.push(end);
            end = end.parent;
        }
    }

    private void addNeighborNodeInOpen(MapInfo mapInfo, Node current) {
        int x = current.coord.x;
        int y = current.coord.y;
        addNeighborNodeInOpen(mapInfo, current, x - 1, y, DIRECT_VALUE);
        addNeighborNodeInOpen(mapInfo, current, x, y - 1, DIRECT_VALUE);
        addNeighborNodeInOpen(mapInfo, current, x + 1, y, DIRECT_VALUE);
        addNeighborNodeInOpen(mapInfo, current, x, y + 1, DIRECT_VALUE);

        addNeighborNodeInOpen(mapInfo, current, x + 1, y + 1, OBLIQUE_VALUE);
        addNeighborNodeInOpen(mapInfo, current, x - 1, y - 1, OBLIQUE_VALUE);
        addNeighborNodeInOpen(mapInfo, current, x + 1, y - 1, OBLIQUE_VALUE);
        addNeighborNodeInOpen(mapInfo, current, x - 1, y + 1, OBLIQUE_VALUE);
    }

    /**
     * 核心移动功能
     */
    private void addNeighborNodeInOpen(MapInfo mapInfo, Node current, int x, int y, int directValue) {
        if (canAddNodeToOpen(mapInfo, x, y)) {//是否可以通过
            Node end = mapInfo.end;//地图的终点
            Coord coord = new Coord(x, y);//需要填入的位置
            int g = current.g + directValue;
            Node child = findNodeInOpen(coord);
            if (child == null) {//如果能填入数字
                int h = calcH(end.coord, coord);
                if (isEndNode(end.coord, coord)) {//如果到了终点
                    child = end;
                    child.parent = current;
                    child.g = g;
                    child.h = h;
                } else {//否则
                    child = new Node(coord, current, g, h);
                }
                openList.add(child);
            } else {
                //如果已经填过的数据，不用理会
            }

        }
    }
}









