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
 * 启发式寻路：
 * 麦哈顿距离，又称马氏距离、出租车距离。实际开发中非常常用,比如地图算最短距离,游戏地图等等。
 * 启发式寻路算法就是根据曼哈顿距离而来,我们可以利用其进行麦哈顿距离估值。
 * <p>
 * 启发式寻路算法的思路：
 * 1)假设从A点到B点,中间有障碍物I。
 * 2)在寻路中,我们肯定要绕过障碍物I到达B点,所以,我们要寻找下一点C为过度,到达B点。所以,引出两个关键说明,即是实际代价(g)
 * 和预估代价(h)。实际代价(g),就是A点到达C点实际需要的距离。
 * 3)当过渡到C点后,我们还需要在C点处对目标点B点的距离进行估值,也就是预估代价(h),因为这里仅仅是举出一个过渡点C,现实开发
 * 中,可不止一个过渡点C喔,有可能有D,E,F....点等。所以,每走到一个新的点上,我们需要再一次预估此点到目标终点的距离。
 * 4)这就是启发式寻路算法,包含实际代价(g)和预估代价(h),所以不难得出表达式f(n)=g(n)+h(n)。
 */
public class HeuristicMainActivity extends AppCompatActivity {

    private static final String TAG = "HeuristicMainActivity";

    private ShowMapView showMapView;

    private Handler handler = null;

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
                handler.post(() ->
                        showMapView.invalidate()
                );

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