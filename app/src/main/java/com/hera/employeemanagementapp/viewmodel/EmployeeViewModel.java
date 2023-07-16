package com.hera.employeemanagementapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hera.employeemanagementapp.model.Employee;
import com.hera.employeemanagementapp.repository.EmployyeRepository;

import java.util.List;

public class EmployeeViewModel extends AndroidViewModel {
    private EmployyeRepository employyeRepository;
    private LiveData<List<Employee>> employeeList;

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        employyeRepository = new EmployyeRepository(application);
        employeeList = employyeRepository.getAllData();
    }

    public void insert(Employee employee) {
        employyeRepository.insertData(employee);
    }

    public void update(Employee employee) {
        employyeRepository.updateData(employee);
    }

    public void delete(Employee employee) {
        employyeRepository.deleteData(employee);
    }

    public LiveData<List<Employee>> getAllEmployee() {
        return employeeList;
    }
}
