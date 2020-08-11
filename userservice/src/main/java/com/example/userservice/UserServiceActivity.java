package com.example.userservice;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UserServiceActivity extends Activity {

    private Button mButton;
    private RecyclerView mRecyclerView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_service);
        mButton = findViewById(R.id.btn);
        mRecyclerView = findViewById(R.id.recyclerView);


        //这是我要添加的git测试
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void test() {

        //单例模式
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        //Builder模式
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("test").setMessage("message").show();

        //原型模式
        Intent intent = new Intent();
        intent.clone();

        //工厂方法模式
        setContentView(new TextView(this));


        //迭代器模式
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator iterator = arrayList.iterator();

        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.entrySet().iterator();

        //模板方法模式
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
        };
        asyncTask.execute();

        //代理模式
        IActivityManager mgr = ActivityManager.getService();

        //适配器模式
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemAdapter itemAdapter = new ItemAdapter();
        mRecyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();

        //享元模式
        Looper.prepare();
        mHandler.sendMessage(new Message());
        Looper.loop();

    }

    //备忘录模式
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
