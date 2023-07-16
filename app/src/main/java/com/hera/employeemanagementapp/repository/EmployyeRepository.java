package com.hera.employeemanagementapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.hera.employeemanagementapp.local.EmployyeDao;
import com.hera.employeemanagementapp.local.EmployyeDatabase;
import com.hera.employeemanagementapp.model.Employee;

import java.util.List;

public class EmployyeRepository {
    private EmployyeDao employyeDao;
    private LiveData<List<Employee>> employyeList;

    public EmployyeRepository(Application application) {

        EmployyeDatabase employyeDatabase = EmployyeDatabase.getInstance(application);
        employyeDao = employyeDatabase.employyeDao();
        employyeList = employyeDao.getAllData();
    }

    public void insertData(Employee employee) {
        new InsertTask(employyeDao).execute(employee);


    }

    public void updateData(Employee employee) {
        new UpdateTask(employyeDao).execute(employee);

    }

    public void deleteData(Employee employee) {
        new DeleteTask(employyeDao).execute(employee);

    }

    public LiveData<List<Employee>> getAllData() {
        return employyeList;
    }

    private static class InsertTask extends AsyncTask<Employee, Void, Void> {
        private EmployyeDao employyeDao;

        public InsertTask(EmployyeDao employyeDao) {
            this.employyeDao = employyeDao;
        }

        @Override
        protected Void doInBackground(Employee... employees) {
            employyeDao.insert(employees[0]);
            return null;
        }

    }

    private static class DeleteTask extends AsyncTask<Employee, Void, Void> {
        private EmployyeDao employyeDao;

        public DeleteTask(EmployyeDao employyeDao) {
            this.employyeDao = employyeDao;
        }

        @Override
        protected Void doInBackground(Employee... employees) {
            employyeDao.delete(employees[0]);
            return null;
        }

    }

    private static class UpdateTask extends AsyncTask<Employee, Void, Void> {
        private EmployyeDao employyeDao;

        public UpdateTask(EmployyeDao employyeDao) {
            this.employyeDao = employyeDao;
        }

        @Override
        protected Void doInBackground(Employee... employees) {
            employyeDao.update(employees[0]);
            return null;
        }

    }


}
