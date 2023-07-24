package com.hera.employeemanagementapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hera.employeemanagementapp.R;
import com.hera.employeemanagementapp.databinding.RowEachItemBinding;
import com.hera.employeemanagementapp.model.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeAdapter extends ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder> {
    private List<Employee> originalList;

    public EmployeeAdapter() {
        super(callback);
        originalList = new ArrayList<>();
    }

    public void setOriginalData(List<Employee> employees) {
        originalList.clear();
        originalList.addAll(employees);
        submitList(employees);
        notifyDataSetChanged();

    }

    public void setFilteredData(List<Employee> employees) {
        submitList(employees);

    }

    public List<Employee> getFilteredList() {
        return new ArrayList<>(getCurrentList());
    }

    public void resetOriginalData() {
        submitList(originalList);
    }

    private static final DiffUtil.ItemCallback<Employee> callback = new DiffUtil.ItemCallback<Employee>() {
        @Override
        public boolean areItemsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
            boolean isNameEqual = oldItem.getName().equals(newItem.getName());
            boolean isPositionEqual = oldItem.getPosition().equals(newItem.getPosition());
            boolean isMailEqual = oldItem.getMail().equals(newItem.getMail());
            boolean isPhoneNumberEqual = oldItem.getPhone_number().equals(newItem.getPhone_number());
            boolean isPhotoEqual = Arrays.equals(oldItem.getPhoto(), newItem.getPhoto());

            return isNameEqual && isPositionEqual && isMailEqual && isPhoneNumberEqual && isPhotoEqual;
        }

    };

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EmployeeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_each_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee currentEmployee = getItem(position);

        holder.binding.tvTitle.setText(currentEmployee.getName());
        holder.binding.tvPosition.setText("~" + currentEmployee.getPosition());
        holder.binding.tvMail.setText(currentEmployee.getMail());
        if (currentEmployee.getPosition().isEmpty()) {
            holder.binding.tvPosition.setVisibility(View.GONE);
        } else {
            holder.binding.tvPosition.setVisibility(View.VISIBLE);
        }

        if (currentEmployee.getPhoto() != null && currentEmployee.getPhoto().length > 0) {
            Bitmap photoBitmap = BitmapFactory.decodeByteArray(currentEmployee.getPhoto(), 0, currentEmployee.getPhoto().length);
            holder.binding.personImage.setImageBitmap(photoBitmap);
        } else {

            if (!isPhotoUpdated(currentEmployee)) {
                holder.binding.personImage.setImageResource(R.drawable.baseline_person_24);
            }
        }
    }

    private boolean isPhotoUpdated(Employee employee) {
        Employee oldEmployee = getCurrentList().get(getCurrentList().indexOf(employee));
        return !Arrays.equals(oldEmployee.getPhoto(), employee.getPhoto());
    }

    public Employee getEmployeeAt(int position) {
        return getItem(position);
    }

    public void updateEmployee(Employee employee) {
        int position = getCurrentList().indexOf(employee);
        if (position != -1) {
            getCurrentList().set(position, employee);
            notifyItemChanged(position);
        }
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        RowEachItemBinding binding;

        public EmployeeViewHolder(View view) {
            super(view);
            binding = RowEachItemBinding.bind(view);
        }
    }
}
