package com.example.jeff.daf.database;

import java.util.List;

/**
 * Created by Jeff on 09/06/2018.
 */

interface Dao {
    public int create(Object item);
    public int update(Object item);
    public int delete(Object item);
    public Object findById(int id);
    public List<?> findAll();
}
