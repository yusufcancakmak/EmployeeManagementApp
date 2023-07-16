package com.hera.employeemanagementapp.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hera.employeemanagementapp.model.Employee;

import java.util.List;

@Dao
public interface EmployyeDao {
    @Insert
    public void insert(Employee employee);
    @Update
    public void update(Employee employee);
    @Delete
    public void delete(Employee employee);

    @Query("SELECT * FROM employees")
    public LiveData<List<Employee>>getAllData();
}
