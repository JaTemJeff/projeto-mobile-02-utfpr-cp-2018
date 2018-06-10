package com.example.jeff.daf.database;

import android.content.Context;

import com.example.jeff.daf.persistencia.DatabaseHelper;
import com.example.jeff.daf.persistencia.DatabaseManager;
import com.example.jeff.daf.modelo.Modo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Jeff on 09/06/2018.
 */

public class ModoDAO implements Dao {

    private DatabaseHelper helper;

    public ModoDAO(Context context) {
        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }
    @Override
    public int create(Object item) {

        int index = -1;
        Modo object = (Modo) item;

        try {
            index = helper.getModoDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public int update(Object item) {
        int index = -1;
        Modo object = (Modo) item;

        try {
            index = helper.getModoDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public int delete(Object item) {
        int index = -1;
        Modo object = (Modo) item;

        try {
            index = helper.getModoDao().delete(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public Object findById(int id) {
        Modo modo = null;

        try {
            modo = helper.getModoDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modo;
    }

    @Override
    public List<?> findAll() {
       List<Modo> items = null;

       try {
            items = helper.getModoDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}

