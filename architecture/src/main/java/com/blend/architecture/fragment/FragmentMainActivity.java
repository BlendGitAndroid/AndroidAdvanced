package com.blend.architecture.fragment;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blend.architecture.R;

import java.util.List;

/**
 * fragment知识点：
 * 1.静态使用Fragment。能嵌套fragment，但是一旦添加就不能在运行时删除。
 * 2.动态使用fragment。
 * 获得FragmentManager对象，通过getSupportFragmentManager()获得FragmentTransaction对象，
 * 通过fm.beginTransaction()调用add()方法或者repalce()方法加载Fragment；最后调用commit()方法提交事务。
 * <p>
 * 注意点：
 * 1.v4包下的fragment。<24版本fragment有重叠问题；onActivityResult分发结果（fragment嵌套）；转场动画问题。
 * 2.getActivity == null。一般在网络请求有延时操作时，界面已经关闭，但是这时获取getActivity可能为null。
 * 解决方案：在onAttach(Activity activity)里赋值，使用mActivity代替getActivity()，保证Fragment即使在onDetach
 * 后，仍持有Activity的引用有引起内存泄露的风险。但是我觉得对于使用者，都要提前判断。
 * 3.Can not perform this action after onSaveInstanceState。
 * 解决方案：
 * 不能在onSaveInstanceState之后的生命周期里面commit fragment；
 * 不要在子线程commit已经走完onSaveInstanceState的fragment。
 * 4.fragment重叠。
 * 原因：屏幕旋转onCreate又会执行，又会重新add一次fragment（fragment恢复了机制又会恢复一个）。
 * 解决方案：
 * 1)onSaveInstanceState activity失去自动存储的功能 super
 * 2)if(savedInstanceState == null){add fragment }
 * <p>
 * 常见操作：
 * 1.add(@IdRes int containerViewId, Fragment fragment,String tag)
 * containerViewId：指定fragment添加到那个viewGroup。
 * 2. fragment传参问题：不要写有参数的构造函数，这是错误的写法。
 * 因为自动恢复机制：只会调用无参的构造函数，其成员变量都没有保存。
 * 解决方案：
 * public void setArguments(@Nullable Bundle args)
 * Bundle getArguments()
 * 3.回退栈问题。
 * addToBackStack(@Nullable String name)
 * popBackStack()：按照栈，入栈的顺序回退：abcd 则dcba
 * popBackStack(@Nullable final String name, final int flags)：回退到特定的地方popBackStack(b,0)，则是回退到ab
 * 单Activity + Fragment问题：
 * 1)自定义
 * class LifeCycleFragment : Fragment(), OnBackPressed，interface OnBackPressed{
 * fun onBackPressed()
 * }
 * 在activity
 * override fun onBackPressed() {
 * super.onBackPressed()
 * //自己管理回退
 * }
 * <p>
 * 2)使用navigation来管理。
 * 4.add 与 replace的区别。
 * add往containerViewId的容器里面添加fragment view，replace将containerViewId的容器里面之前添加的view全部清空，
 * add replace千万不要混合使用，要么全部用add，要么全部用replace。
 * <p>
 * <p>
 * Fragment的原理知识：
 */
public class FragmentMainActivity extends AppCompatActivity {

    private static final String TAG = "FragmentMainActivity";

    private Button fragmentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        setContentView(R.layout.activity_fragment_main);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.rightFragment, RightFragment.newInstance("blend"), RightFragment.class.getName());
            transaction.addToBackStack(null);
            transaction.commitNow();
        }

        fragmentBtn = findViewById(R.id.fragmentBtn);
        fragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.rightFragment, new LeftFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState: ");

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        FragmentManager fm = getSupportFragmentManager();
        List fragmentList = fm.getFragments();
        if (fragmentList.size() == 0) {
            super.onSaveInstanceState(outState, outPersistentState);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // private Handler mHandler = new Handler() {
    //     @Override
    //     public void handleMessage(Message msg) {
    //         super.handleMessage(msg);
    //         FragmentManager fragmentManager = getSupportFragmentManager();
    //         FragmentTransaction transaction = fragmentManager.beginTransaction();
    //         transaction.add(R.id.rightFragment, RightFragment.newInstance(mHandler, "Blend"), RightFragment.class.getName());
    //         transaction.commit();
    //     }
    // };
}