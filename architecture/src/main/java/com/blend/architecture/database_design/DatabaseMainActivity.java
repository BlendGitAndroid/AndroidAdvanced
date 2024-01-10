package com.blend.architecture.database_design;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

/**
 * 数据库架构设计的思路：
 * 使用SQLiteDatabase类，封装了一些操作数据库的API，使用该类可以完成对数据进行添加(Create)、查询(Retrieve)、更新(Update)
 * 和删除(Delete)操作（这些操作简称为CRUD）。
 * execSQL()方法可以执行insert、delete、update和CREATE TABLE之类有更改行为的SQL语句；rawQuery()方法用于执行select语句。
 * <p>
 * 创建表：因为不知道具体的类，使用泛型来表示某一个数据库类，那么就需要通过反射来拿到这个类中所有的成员变量类型,若有注解，则新建表
 * 的表名和列名就是注解的名字，若没有注解，则是类名和成员变量名，并将数据库列名和成员变量名一一对应缓存成Map。
 * 增删改查：首先根据传入的对象，通过缓存的Map拿到列名和变量名，通过变量名拿到变量值，从而拿到列名和相应的值，进行增删改查
 * <p>
 * SQLiteOpenHelper中的实现,使用SQLiteDatabase类，封装了一些操作数据库的API，使用该类可以完成对数据进行CURD.
 * <p>
 * 这个数据库框架的原理是:总体都是使用SQLiteDatabase类,使用注解来获取对象类的信息,将类名作为表名,将成员变量名作为列名,将成员变量的值作为列的值.
 */
public class DatabaseMainActivity extends AppCompatActivity {

    private static final String TAG = "DatabaseMainActivity";

    private int index = 0;

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
        baseDao.insert(new Person(index++, "xu", "123"));
        Toast.makeText(this, "插入成功！", Toast.LENGTH_SHORT).show();
    }

    public void clickUpdate(View view) {
        BaseDaoNewImpl<Person> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoNewImpl.class, Person.class);
        Person person = new Person();
        person.setName("jiangzuo");
        Person where = new Person();
        where.setId(3);
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
        where.setId(0);
        List<Person> query = baseDao.query(where);
        if (query != null) {
            for (Person person : query) {
                Log.i(TAG, person.getName() + "");
            }
        }
    }
}