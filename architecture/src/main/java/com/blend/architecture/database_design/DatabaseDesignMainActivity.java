package com.blend.architecture.database_design;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blend.architecture.MyApplication;
import com.blend.architecture.R;

public class DatabaseDesignMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_design_main);

        UserDao userDao = MyApplication.getDaoSession().getUserDao();
        userDao.insert(new User(001, "xuhai", "123"));
    }
}