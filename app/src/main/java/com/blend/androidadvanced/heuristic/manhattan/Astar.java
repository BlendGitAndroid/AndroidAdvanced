package com.blend.androidadvanced.heuristic.manhattan;

import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Astar {
    public final static int DIRECT_VALUE = 10;
    public final static int OBLIQUE_VALUE = 14;
    //开放列表  白格子
    private Queue<Node> openList = new PriorityQueue<Node>();   //开放列表就是下一步需要寻址的格子，PriorityQueue会按照大小排序的队列
    //关闭列表  黄格子
    private List<Node> closeList = new ArrayList<Node>();   //关闭列表就是已经被选择过的格子

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

    /**
     * 如果关闭列表中有终点
     *
     * @param x
     * @param y
     * @return
     */
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
        //判断是否已经被选择过的，判断是否在关闭列表里面
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
        openList.add(mapInfo.start);    //将开始格子放入开放列表
        //开始向周围扩展
        moveNodes(mapInfo);
    }

    private void moveNodes(MapInfo mapInfo) {
        while (!openList.isEmpty()) {
            if (isCoordInClose(mapInfo.end.coord)) {//如果已经是终点了
                //统计结果
                calcPath(mapInfo.end);
            }
            //把open中最小的一个取出来 ,放到关闭列表close中
            Node current = openList.poll();
            Log.e("moveNodes: ", current.g + " --- " + current.h);
            closeList.add(current);
            //开始扩展
            addNeighborNodeInOpen(mapInfo, current);
        }
    }

    /**
     * 计算结果
     *
     * @param end
     */
    private void calcPath(Node end) {
        MapUtils.path = new Path();
        if (end != null) {
            MapUtils.path.moveTo(end.coord.x * 80 + 40, end.coord.y * 80 + 40); //将点放到方格的中间，设置绘制的开始点
        }
        while (end != null) { //把结果入栈
            MapUtils.path.lineTo(end.coord.x * 80 + 40, end.coord.y * 80 + 40); //从终点开始绘制
            MapUtils.result.push(end);  //入栈操作
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
     * 核心移动功能，将点放入开放列表中
     */
    private void addNeighborNodeInOpen(MapInfo mapInfo, Node current, int x, int y, int directValue) {
        if (canAddNodeToOpen(mapInfo, x, y)) {//是否可以通过
            Node end = mapInfo.end;//地图的终点
            Coord coord = new Coord(x, y);//需要填入的位置坐标
            int g = current.g + directValue;    //生成新坐标的实际代价
            Node child = findNodeInOpen(coord); //从开放列表中选择
            if (child == null) {    //如果不在开放列表中，则填入到开放列表中
                int h = calcH(end.coord, coord);    //计算预估代价
                if (isEndNode(end.coord, coord)) { //如果到了终点
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









