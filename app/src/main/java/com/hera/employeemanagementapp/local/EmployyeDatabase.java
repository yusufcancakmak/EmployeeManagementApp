package com.hera.employeemanagementapp.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hera.employeemanagementapp.model.Employee;

@Database(entities = Employee.class,version = 6)
public abstract class EmployyeDatabase extends RoomDatabase {
    public abstract EmployyeDao employyeDao();

    private static EmployyeDatabase instance;
    public static synchronized EmployyeDatabase getInstance(Context context){
        if (instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(),
                    EmployyeDatabase.class,"employye_database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
