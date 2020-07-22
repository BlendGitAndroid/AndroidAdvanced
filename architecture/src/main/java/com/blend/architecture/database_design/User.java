package com.blend.architecture.database_design;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 *会自动生成一些数据库相关类，这些类在build.gradle里设置的目录下，而且实体类里面也会自动生成get/set方法
 * 这里有多少个@Entity注释的实体类就会生成多少个相关的XXXDao类，XXXDao类里提供对实体类对应的表单的CRUD的操作方法，
 * 即ORM里提供以面向对象的方式来处理关系型数据库，不需要我们去写sql语句。
 */
@Entity
public class User {

    @Id(autoincrement = true)
    private int id; //自增长的id类型一定是Long/long类型，否则会报错误
    private String name;
    private String password;

    @Generated(hash = 258842593)
    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
