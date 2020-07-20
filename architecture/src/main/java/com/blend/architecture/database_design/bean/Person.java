package com.blend.architecture.database_design.bean;

import com.blend.architecture.database_design.annotation.DbField;
import com.blend.architecture.database_design.annotation.DbTable;

@DbTable("tb_person")
public class Person {

    @DbField("_id")
    private Integer id;
    private String name;
    private String password;

    public Person() {

    }

    public Person(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
