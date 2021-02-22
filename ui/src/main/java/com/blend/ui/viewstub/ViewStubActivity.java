package com.blend.ui.viewstub;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.blend.ui.R;

/**
 * 为什么ViewStub只能调用inflate一次，就是因为getParent的时候，
 */
public class ViewStubActivity extends AppCompatActivity {

    private ViewStub viewStub;
    private TextView textView;
    private View inflate;
    private ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stub);
        viewStub = findViewById(R.id.vs);
    }


    public void inflate(View view) {
        if (inflate == null) {
            inflate = viewStub.inflate(); //inflate只会进行一次，当第二次调用的时候，就会抛异常；也可以try catch进行处理
            constraintLayout = findViewById(R.id.inflatedStart);    //初始化过后，就相当于这个ViewStub已经添加到布局了
            textView = viewStub.findViewById(R.id.hello_tv);//获取到的textview是空的；
            textView = constraintLayout.findViewById(R.id.hello_tv);
            textView = findViewById(R.id.hello_tv);
        }
    }

    public void setData(View view) {
        if (constraintLayout != null) {
            textView = constraintLayout.findViewById(R.id.hello_tv);
            textView.setText("HAVE DATA !!!");
        }
    }

    public void hide(View view) {
        viewStub.setVisibility(View.GONE);
//        if (constraintLayout != null){
//            constraintLayout.setVisibility(View.GONE);
//        }
    }

    public void show(View view) {
        viewStub.setVisibility(View.VISIBLE);
//        if (constraintLayout != null){
//            constraintLayout.setVisibility(View.VISIBLE);
//        }
    }

}