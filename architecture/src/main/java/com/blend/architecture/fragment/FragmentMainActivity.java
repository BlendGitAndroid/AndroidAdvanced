package com.blend.architecture.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.blend.architecture.R;

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
 */
public class FragmentMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.rightFragment, RightFragment.newInstance(), RightFragment.class.getName());
            transaction.commit();
        }
    }
}