package com.blend.architecture.database_design;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class User {

    public int id;
    public String name;
    public String password;
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
