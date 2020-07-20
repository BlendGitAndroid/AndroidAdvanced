package com.blend.architecture.database_design.db;

import java.util.List;

public interface IBaseDao<T> {

    //增
    long insert(T entity);

    //删
    long update(T entity, T where);

    //改
    int delete(T where);

    //查
    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

}
