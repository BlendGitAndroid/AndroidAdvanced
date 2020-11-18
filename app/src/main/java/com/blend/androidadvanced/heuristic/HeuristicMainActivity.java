package com.blend.androidadvanced.heuristic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blend.androidadvanced.R;
import com.blend.androidadvanced.heuristic.manhattan.Astar;
import com.blend.androidadvanced.heuristic.manhattan.MapInfo;
import com.blend.androidadvanced.heuristic.manhattan.MapUtils;
import com.blend.androidadvanced.heuristic.manhattan.Node;
import com.blend.androidadvanced.heuristic.manhattan.ShowMapView;

import static com.blend.androidadvanced.heuristic.manhattan.MapUtils.endCol;
import static com.blend.androidadvanced.heuristic.manhattan.MapUtils.endRow;
import static com.blend.androidadvanced.heuristic.manhattan.MapUtils.map;
import static com.blend.androidadvanced.heuristic.manhattan.MapUtils.result;
import static com.blend.androidadvanced.heuristic.manhattan.MapUtils.startCol;
import static com.blend.androidadvanced.heuristic.manhattan.MapUtils.startRow;
import static com.blend.androidadvanced.heuristic.manhattan.MapUtils.touchFlag;


/**
 * 启发式寻路
 */
public class HeuristicMainActivity extends AppCompatActivity {

    private static final String TAG = "HeuristicMainActivity";

    ShowMapView showMapView;

    Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heuristic_main);

        handler = new Handler(Looper.getMainLooper());
        showMapView = (ShowMapView) findViewById(R.id.show);
    }

    public void cal(View view) {
        MapInfo info = new MapInfo(map, map[0].length, map.length, new Node(startCol, startRow), new Node(endCol, endRow));
        Log.i(TAG, "start=" + startRow + " " + startCol);
        Log.i(TAG, "end=" + endRow + " " + endCol);
        new Astar().start(info);
        new MoveThread(showMapView).start();

    }

    public void reset(View view) {
        MapUtils.path = null;
        MapUtils.result.clear();
        touchFlag = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 2) {
                    map[i][j] = 0;
                }
            }
        }
        showMapView.invalidate();

    }

    class MoveThread extends Thread {
        ShowMapView showMapView;

        public MoveThread(ShowMapView showMapView) {
            this.showMapView = showMapView;
        }

        @Override
        public void run() {
            while (result.size() > 0) {
                Log.e(TAG, result.size() + "");
                Node node = result.pop();
                map[node.coord.y][node.coord.x] = 2;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showMapView.invalidate();
                    }
                });

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                map[node.coord.y][node.coord.x] = 0;

            }
            MapUtils.touchFlag = 0;
        }
    }
}