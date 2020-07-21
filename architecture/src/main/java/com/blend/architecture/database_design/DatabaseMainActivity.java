package com.blend.architecture.database_design;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blend.architecture.R;
import com.blend.architecture.database_design.bean.Person;
import com.blend.architecture.database_design.daopackage.GreenDaoMainActivity;
import com.blend.architecture.database_design.db.BaseDao;
import com.blend.architecture.database_design.db.BaseDaoFactory;
import com.blend.architecture.database_design.db.BaseDaoNewImpl;

import java.util.List;

public class DatabaseMainActivity extends AppCompatActivity {

    private static final String TAG = "DatabaseMainActivity";

    private Button greenDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_main);
        greenDao = findViewById(R.id.greenDao);
        greenDao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DatabaseMainActivity.this, GreenDaoMainActivity.class));
            }
        });
    }

    public void insert(View view) {
        BaseDao<Person> baseDao = BaseDaoFactory.getInstance().getBaseDao(Person.class);
        baseDao.insert(new Person(1, "xu", "123"));
        Toast.makeText(this, "插入成功！", Toast.LENGTH_SHORT).show();
    }

    public void clickUpdate(View view) {
        BaseDaoNewImpl<Person> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoNewImpl.class, Person.class);
        Person person = new Person();
        person.setName("jiangzuo");
        Person where = new Person();
        where.setId(1);
        long update = baseDao.update(person, where);
        Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    public void clickDelete(View view) {
        BaseDao<Person> baseDao = BaseDaoFactory.getInstance().getBaseDao(Person.class);
        Person where = new Person();
        where.setId(1);
        baseDao.delete(where);
        Toast.makeText(this, "删除成功！", Toast.LENGTH_SHORT).show();
    }

    public void clickSelect(View view) {
        BaseDao<Person> baseDao = BaseDaoFactory.getInstance().getBaseDao(Person.class);
        Person where = new Person();
        where.setId(1);
        List<Person> query = baseDao.query(where);
        if (query != null) {
            for (Person person : query) {
                Log.i(TAG, person.getName() + "");
            }
        }
    }
}