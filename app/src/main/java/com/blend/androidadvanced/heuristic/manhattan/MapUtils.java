package com.blend.androidadvanced.heuristic.manhattan;


import android.graphics.Path;

import java.util.Stack;

public class MapUtils {

    public static int startRow = 0;
    public static int startCol = 0;
    public static int endRow = 0;
    public static int endCol = 0;
    public static int touchFlag = 0;    //用于判断已经确定起始点和终止点，通过刷新按钮重置
    public static Stack<Node> result = new Stack<>();
    public static Path path;

    public final static int WALL = 1; //  障碍
    public final static int PATH = 2; // 路径

    //0表示草地，1表示墙
    public static int[][] map = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1},
            {0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
            {0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
            {1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1},
            {0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1},
            {1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1},
            {0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0},
            {1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1},
            {1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1}
    };

    public static int mapRowSize = map.length;
    public static int mapColSize = map[0].length;


    /**
     * 打印地图
     */
    public static void printMap(int[][] maps) {
        for (int i = 0; i < maps.length; i++) {
            for (int j = 0; j < maps[i].length; j++) {
                System.out.print(maps[i][j] + " ");
            }
            System.out.println();
        }
    }

}
