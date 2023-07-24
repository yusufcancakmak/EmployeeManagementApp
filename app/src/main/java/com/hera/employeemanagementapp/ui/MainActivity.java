package com.hera.employeemanagementapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hera.employeemanagementapp.R;
import com.hera.employeemanagementapp.adapter.EmployeeAdapter;
import com.hera.employeemanagementapp.databinding.ActivityMainBinding;
import com.hera.employeemanagementapp.helper.Constans;
import com.hera.employeemanagementapp.model.Employee;
import com.hera.employeemanagementapp.viewmodel.EmployeeViewModel;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private EmployeeViewModel employeeViewModel;
    private EmployeeAdapter employeeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        employeeViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory)
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).
                get(EmployeeViewModel.class);

        initControl();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure to delete this employee?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        employeeViewModel.delete(employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()));
                        Toast.makeText(MainActivity.this, "employyed deleted", Toast.LENGTH_LONG).show();
                    });
                    builder.setNegativeButton("No", (dialogInterface, i) -> {
                        employeeAdapter.notifyDataSetChanged();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    binding.searchView.setQuery("", true);
                    Intent intent = new Intent(MainActivity.this, DataInsertActivity.class);
                    intent.putExtra(Constans.Key_type, "update");
                    intent.putExtra(Constans.Key_name, employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()).getName());
                    intent.putExtra(Constans.Key_position, employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()).getPosition());
                    intent.putExtra(Constans.Key_mail, employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()).getMail());
                    intent.putExtra(Constans.Key_phone, employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()).getPhone_number());
                    intent.putExtra(Constans.Key_image, employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()).getPhoto());
                    intent.putExtra(Constans.Key_adress, employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()).getAddress());
                    intent.putExtra(Constans.Key_id, employeeAdapter.getEmployeeAt(viewHolder.getAdapterPosition()).getId());
                    startActivityForResult(intent, 2);


                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorTextPrimary))
                        .addSwipeLeftActionIcon(R.drawable.baseline_update_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorThemeExtra))
                        .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(binding.rvDashboard);

    }


    private void filterList(String text) {
        if (text.isEmpty()) {
            employeeAdapter.resetOriginalData();
        } else {
            List<Employee> filteredList = new ArrayList<>();

            List<Employee> fullList = employeeAdapter.getFilteredList();

            for (Employee employee : fullList) {
                if (employee.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(employee);
                }
            }
            if (filteredList.isEmpty()) {
                Snackbar.make(binding.getRoot(), "No item found", Snackbar.LENGTH_SHORT).show();
            }
            employeeAdapter.setFilteredData(filteredList);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            String name = data.getStringExtra(Constans.Key_name);
            String position = data.getStringExtra(Constans.Key_position);
            String mail = data.getStringExtra(Constans.Key_mail);
            String phone = data.getStringExtra(Constans.Key_phone);
            byte[] photo = data.getByteArrayExtra(Constans.Key_image);
            String address = data.getStringExtra(Constans.Key_adress);
            Employee employee = new Employee(name, position, mail, phone, photo, address);
            boolean isInserted = employeeViewModel.insertIfEmployeeNotExists(employee);
            if (isInserted) {
                Snackbar.make(binding.getRoot(), "Employee Added", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(binding.getRoot(), "Employee with the same data already exists", Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            String name = data.getStringExtra(Constans.Key_name);
            String position = data.getStringExtra(Constans.Key_position);
            String mail = data.getStringExtra(Constans.Key_mail);
            String phone = data.getStringExtra(Constans.Key_phone);
            byte[] photo = data.getByteArrayExtra(Constans.Key_image);
            String address = data.getStringExtra(Constans.Key_adress);
            Employee updatedEmployee = new Employee(name, position, mail, phone, photo, address);
            updatedEmployee.setId(data.getIntExtra(Constans.Key_id, 0));
            employeeViewModel.update(updatedEmployee);
            employeeAdapter.updateEmployee(updatedEmployee);
            employeeAdapter.notifyDataSetChanged();
            Snackbar.make(binding.getRoot(), "Employye Updated", Snackbar.LENGTH_SHORT).show();

        }

    }

    public void initData() {
        binding.rvDashboard.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDashboard.setHasFixedSize(true);
        employeeAdapter = new EmployeeAdapter();
        binding.rvDashboard.setAdapter(employeeAdapter);
        employeeViewModel.getAllEmployee().observe(this, new Observer<List<Employee>>() {
            @Override
            public void onChanged(List<Employee> employees) {
                employeeAdapter.setOriginalData(employees);
            }
        });
    }

    public void initControl() {
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        binding.floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DataInsertActivity.class);
                intent.putExtra(Constans.Key_type, "addEmployee");
                startActivityForResult(intent, 1);
            }
        });
        initData();
        employeeViewModel.getAllEmployee().observe(this, new Observer<List<Employee>>() {
            @Override
            public void onChanged(List<Employee> employees) {
                employeeAdapter.submitList(employees);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        employeeAdapter.notifyDataSetChanged();
    }
}




