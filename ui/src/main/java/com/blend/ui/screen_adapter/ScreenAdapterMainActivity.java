package com.blend.ui.screen_adapter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.blend.ui.R;

public class ScreenAdapterMainActivity extends AppCompatActivity {


    //声明本次使用到的java类
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    RightFragment rightFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_adapter_main);

        //获取swdp方案
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        float density = dm.density;
        float widthDP = widthPixels / density;
        int heightPixels = dm.heightPixels;
        DisplayMetrics dm2 = new DisplayMetrics();


        getWindowManager().getDefaultDisplay().getRealMetrics(dm2);

        int height = dm2.heightPixels;
        float heightdp = (height-heightPixels) / density;
        Log.i("barry","widthPixels:"+widthPixels);
        Log.i("barry","height:"+height);
        Log.i("barry","heightdp:"+heightdp);
        Log.i("barry","heightPixels:"+heightPixels);
        Log.i("barry","density:"+density);
        Log.i("barry","sw widthDP:"+widthDP);

        if(widthDP > 360){
            fragmentManager=getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            rightFragment = new RightFragment();
            fragmentTransaction.add(R.id.right,rightFragment);
            fragmentTransaction.commit();

        }
    }
}
