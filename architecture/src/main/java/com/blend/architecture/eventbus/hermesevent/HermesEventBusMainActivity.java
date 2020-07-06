package com.blend.architecture.eventbus.hermesevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.core.Hermes;
import com.blend.architecture.eventbus.manager.UserManager;
import com.blend.architecture.eventbus.model.Friend;

public class HermesEventBusMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes_event_bus_main);

        Hermes.getDefault().register(UserManager.class);

        UserManager.getInstance().setFriend(new Friend("xuhai", 18));

    }

    public void change(View view) {
        startActivity(new Intent(this, HermesEventBusSecondActivity.class));
    }

    public void getPerson(View view) {
        Toast.makeText(this, UserManager.getInstance().getFriend().toString(), Toast.LENGTH_SHORT).show();
    }

}